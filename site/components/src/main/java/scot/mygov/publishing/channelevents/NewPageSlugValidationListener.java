package scot.mygov.publishing.channelevents;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.pagecomposer.jaxrs.api.ChannelEventListenerRegistry;
import org.hippoecm.hst.pagecomposer.jaxrs.api.PageCreateContext;
import org.hippoecm.hst.pagecomposer.jaxrs.api.PageCreateEvent;
import org.hippoecm.hst.pagecomposer.jaxrs.services.exceptions.ClientError;
import org.hippoecm.hst.pagecomposer.jaxrs.services.exceptions.ClientException;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.sluglookup.SlugLookupPaths;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Listens for new Experience Pages being created in Channel Manager and rejects the action if the page's
 * path clashes with the slug of an existing content document. This is checked via the slug lookup tree
 * (maintained by SlugMaintenanceListener in the cms module) rather than a query over content documents, both
 * to avoid building a query string from user-controlled input and to mirror the equivalent check for the
 * opposite direction, {@code UrlValidationUtils.slugClashesWithXPage} in the cms module (which cannot be
 * reused here directly since cms and site are separate deployable webapps).
 */
public class NewPageSlugValidationListener {

    private static final Logger LOG = LoggerFactory.getLogger(NewPageSlugValidationListener.class);

    private static final String TYPE_GLOBAL = "global";

    private static final String[] MOUNTS = {"live", "preview"};

    private static final String SLUGLOOKUP_PATH = "sluglookup:path";

    @Subscribe
    public void onPageCreateEvent(PageCreateEvent event) {

        if (event.getException() != null) {
            return;
        }

        PageCreateContext context = event.getPageActionContext();
        Node newSiteMapItemNode = context.getNewSiteMapItemNode();
        if (newSiteMapItemNode == null) {
            return;
        }

        try {
            String slug = newSiteMapItemNode.getName();
            Mount editingMount = context.getEditingMount();
            String site = StringUtils.substringAfterLast(editingMount.getContentPath(), "/");
            Session session = context.getRequestContext().getSession();

            String clash = slugLookupClash(session, site, slug);
            LOG.info("PageCreateEvent: slug={}, site={}, clash={}", slug, site, clash);

            if (clash != null) {
                String clashingContentPath = clashingContentPath(session, clash);
                String message = clashingContentPath != null
                        ? "Slug '" + slug + "' clashes with the slug of an existing page at '" + clashingContentPath + "'"
                        : "Slug '" + slug + "' clashes with the slug of an existing page";
                event.setException(new ClientException(message, ClientError.ITEM_EXISTS_OUTSIDE_WORKSPACE));
            }
        } catch (RepositoryException e) {
            LOG.error("Failed to validate slug for new page", e);
        }
    }

    String slugLookupClash(Session session, String site, String slug) throws RepositoryException {
        for (String mount : MOUNTS) {
            String lookupPath = SlugLookupPaths.slugLookupPath(slug, site, TYPE_GLOBAL, mount);
            if (session.nodeExists(lookupPath)) {
                return lookupPath;
            }
        }
        return null;
    }

    /**
     * The slug lookup node's own path is an internal implementation detail (a hash bucket, not a
     * meaningful location) so look up the content path it points at for the error message. The property
     * can be missing on a stale lookup node, in which case there is nothing more specific to report.
     */
    String clashingContentPath(Session session, String lookupPath) throws RepositoryException {
        Node lookupNode = session.getNode(lookupPath);
        return lookupNode.hasProperty(SLUGLOOKUP_PATH) ? lookupNode.getProperty(SLUGLOOKUP_PATH).getString() : null;
    }

    public void init() {
        ChannelEventListenerRegistry.get().register(this);
    }

    public void destroy() {
        ChannelEventListenerRegistry.get().unregister(this);
    }
}
