package scot.mygov.publishing.linkprocessors;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.linking.HstLinkProcessorTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

/**
 * Link processor for site furniture.
 *
 * Site furniture appears at the root of the site.  Those in the folder "footer" will be displayed in the footer of
 * every page.
 */
public class SiteFurnitureLinkProcessor extends HstLinkProcessorTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(SiteFurnitureLinkProcessor.class);

    private static final String SITE_FURNITURE = "site-furniture";

    SessionSource sessionSource = link -> RequestContextProvider.get().getSession();

    @Override
    protected HstLink doPostProcess(HstLink link) {

        if (!isSiteFurniture(link)) {
            return link;
        }

        link.setPath(link.getPathElements()[link.getPathElements().length - 1]);
        return link;
    }

    boolean isSiteFurniture(HstLink link) {
        return SITE_FURNITURE.equals(link.getPathElements()[0]);
    }

    /**
     * Find the node for this url
     */
    @Override
    protected HstLink doPreProcess(HstLink link) {

        if (link.getPathElements().length != 1) {
            return link;
        }

        try {
            String slug = link.getPathElements()[0];
            Session session = sessionSource.getSession(link);
            Node handle = getHandleByName(slug, session);
            if (handle == null) {
                return link;
            }

            // the path has to be relative to the root of the site
            String handlePath = handle.getPath();
            if (!handlePath.contains(SITE_FURNITURE + "/")) {
                return link;
            }

            String path = StringUtils.substringAfter(handlePath, SITE_FURNITURE + "/");
            link.setPath(SITE_FURNITURE + "/" + path);
            return link;
        } catch (RepositoryException e) {
            LOG.warn("Exception trying to process link: \"{}\"", link.getPath(), e);
            return link;
        }
    }

    private Node getHandleByName(String name, Session session) throws RepositoryException {
        String sql = String.format("SELECT * FROM publishing:basedocument WHERE jcr:name LIKE '%s'", name);
        QueryResult result = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL).execute();
        if (result.getNodes().getSize() == 0) {
            return null;
        }
        return result.getNodes().nextNode().getParent();
    }

    // This is just here to support unit testing (to get around Hippos use of a static method)
    interface SessionSource {
        Session getSession(HstLink link) throws RepositoryException;
    }
}