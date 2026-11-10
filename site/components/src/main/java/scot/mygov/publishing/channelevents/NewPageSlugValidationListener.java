package scot.mygov.publishing.channelevents;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.pagecomposer.jaxrs.api.ChannelEventListenerRegistry;
import org.hippoecm.hst.pagecomposer.jaxrs.api.PageCreateContext;
import org.hippoecm.hst.pagecomposer.jaxrs.api.PageCreateEvent;
import org.hippoecm.hst.pagecomposer.jaxrs.services.exceptions.ClientError;
import org.hippoecm.hst.pagecomposer.jaxrs.services.exceptions.ClientException;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import java.util.regex.Pattern;

/**
 * Listens for new Experience Pages being created in Channel Manager and rejects the action if the page's
 * path clashes with the slug of an existing content document. The corresponding check for the opposite
 * direction (a content document's slug clashing with an existing Experience Page) lives in the cms module's
 * {@code UrlValidationUtils.slugClashesWithXPage}; it cannot be reused here directly since cms and site are
 * separate deployable webapps.
 */
public class NewPageSlugValidationListener {

    private static final Logger LOG = LoggerFactory.getLogger(NewPageSlugValidationListener.class);

    private static final Pattern SAFE_CONTENT_PATH = Pattern.compile("/content/documents/[a-zA-Z0-9_-]+");

    private static final Pattern SAFE_SLUG = Pattern.compile("[a-z0-9-]+");

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
            String contentPath = editingMount.getContentPath();
            Session session = context.getRequestContext().getSession();

            String clash = documentWithSlug(session, contentPath, slug);
            LOG.info("PageCreateEvent: slug={}, contentPath={}, clash={}", slug, contentPath, clash);

            if (clash != null) {
                event.setException(new ClientException(
                        "Slug '" + slug + "' clashes with the slug of an existing page at '" + clash + "'",
                        ClientError.ITEM_EXISTS_OUTSIDE_WORKSPACE));
            }
        } catch (RepositoryException e) {
            LOG.error("Failed to validate slug for new page", e);
        }
    }

    /**
     * Find a document under the content path whose slug is the same as the new page's.
     *
     * Hippo does not support bind variables (getQOMFactory throws, and a $name in a query string is replaced by
     * plain text substitution), so the query has to be a string. To keep that safe, both values are checked
     * against strict patterns before they go into it: a slug can only contain lower case letters, numbers and
     * hyphens (the same rule SlugValidator applies to publishing:slug, so a name outside it cannot clash with any
     * document), and the content path has to be a site folder under /content/documents.
     */
    String documentWithSlug(Session session, String contentPath, String slug) throws RepositoryException {
        if (!SAFE_CONTENT_PATH.matcher(contentPath).matches()) {
            throw new IllegalArgumentException("Unexpected content path: " + contentPath);
        }

        if (!SAFE_SLUG.matcher(slug).matches()) {
            return null;
        }

        String xpath = String.format(
                "/jcr:root%s//element(*, publishing:base)[@publishing:slug = '%s']", contentPath, slug);
        NodeIterator nodes = session.getWorkspace().getQueryManager()
                .createQuery(xpath, Query.XPATH)
                .execute()
                .getNodes();
        return nodes.hasNext() ? nodes.nextNode().getPath() : null;
    }

    public void init() {
        ChannelEventListenerRegistry.get().register(this);
    }

    public void destroy() {
        ChannelEventListenerRegistry.get().unregister(this);
    }
}
