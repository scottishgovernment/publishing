package scot.mygov.publishing.components;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.util.HstResponseUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

/**
 * Component used for unmatched url.  First tries to find a url alias for the url.  If one exist then a redirect is
 * issued, otherwise we serve the 404 page.
 */
public class URLAliasComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(URLAliasComponent.class);

    Redirector redirector = (req, resp, node) -> {
        HstLinkCreator linkCreator = req.getRequestContext().getHstLinkCreator();
        HstLink redirectTolink = linkCreator.create(node, req.getRequestContext());
        String url = redirectTolink.getPath();
        LOG.info("Redirecting to url alias {} -> {}", req.getPathInfo(), url);
        HstResponseUtils.sendPermanentRedirect(req, resp, url);
    };

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        request.setModel("document", request.getRequestContext().getContentBean());

        // check if this url is a known url alias
        Node node = findUrlByAlias(request);

        if (node != null) {
            redirector.redirect(request, response, node);
        }
    }

    private Node findUrlByAlias(HstRequest request)  {
        try {
            return findRedirectNode(request);
        } catch (RepositoryException e) {
            LOG.error("Failed to find url alias {}", request.getPathInfo(), e);
            return null;
        }
    }

    Node findRedirectNode(HstRequest request) throws RepositoryException {

        QueryResult result = executeFindNodesQuery(request);

        if (result.getNodes().getSize() == 0) {
            return null;
        }

        return result.getNodes().nextNode();
    }

    QueryResult executeFindNodesQuery(HstRequest request) throws RepositoryException {
        String xpath = xpath(request);
        Session session = request.getRequestContext().getSession();
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        return query.execute();
    }

    String xpath(HstRequest request) {
        Mount mount = request.getRequestContext().getResolvedMount().getMount();
        String xpathTemplate = "/jcr:root%s//element(*, publishing:base)[publishing:urlAliases = '%s'][hippostd:state = '%s']";

        // url aliases do not contain a trailing slash so remove one if it is present
        String path = StringUtils.stripEnd(request.getPathInfo(), "/");

        return String.format(
                xpathTemplate,
                mount.getContentPath(),
                path,
                targetState(mount));
    }

    String targetState(Mount mount) {
        // if this is the live mount then look for published documents, otherwise look for draft items
        return "live".equals(mount.getType())
                ? "published"
                : "draft";
    }

    /**
     * Added to aid unit testing
     */
    public interface Redirector {
        void redirect(HstRequest request, HstResponse response, Node node);
    }


}
