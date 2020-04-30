package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
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

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {

        // check if this url is a known url alias
        String url = findUrlByAlias(request);

        if (url != null) {
            LOG.info("Redirecting to url alias {} -> {}", request.getPathInfo(), url);
            HstResponseUtils.sendPermanentRedirect(request, response, url);
            return;
        }
    }

    private String findUrlByAlias(HstRequest request)  {
        try {
            Node redirectToNode = findRedirectNode(request);
            return redirectToNode == null ? null : linkTo(request, redirectToNode);
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

        if (result.getNodes().getSize() > 1) {
            LOG.warn("got more than one alias for {}", request.getPathInfo());
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
        String xpathTemplate =
                "/jcr:root%s//element(*, publishing:base)" +
                "[publishing:urlAliases = '%s']" +
                "[hippostd:state = '%s']";
        return String.format(
                xpathTemplate,
                mount.getContentPath(),
                request.getPathInfo(),
                targetState(mount));
    }

    String targetState(Mount mount) {
        // if this is the live mount then look for published documents, otherwise look for draft items
        return mount.getType().equals("live")
                ? "published"
                : "draft";
    }

    String linkTo(HstRequest request, Node redirectToNode) {
        HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();
        HstLink redirectTolink = linkCreator.create(redirectToNode, request.getRequestContext());
        return redirectTolink.getPath();
    }
}
