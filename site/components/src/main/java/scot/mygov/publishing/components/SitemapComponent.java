package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.onehippo.forge.sitemap.components.model.Url;
import org.onehippo.forge.sitemap.components.model.Urlset;
import org.onehippo.forge.sitemap.generator.SitemapGenerator;

import java.util.Calendar;

/**
 * Component to produce a sitemap.xml.  This class uses code from the forge plugin to produce the XML.
 *
 * Currently this component is only suitable for smallish sites (< 500 pages).
 *
 * TODO:
 * - do we want the /index ?
 * - do we want to include the home page?
 */
public class SitemapComponent extends BaseHstComponent {

    private static final int MAX_ITEMS_PER_SITEMAP = 500;

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        Urlset urlset = generateSitemap(request);
        request.setAttribute("sitemap", SitemapGenerator.toString(urlset));
    }

    static Urlset generateSitemap(HstRequest request) {
        try {
            return doGenerateSitemap(request);
        } catch (RepositoryException e) {
            throw new HstComponentException(e);
        }
    }

    private static Urlset doGenerateSitemap(HstRequest request) throws RepositoryException {
        HstRequestContext context = request.getRequestContext();
        HstLinkCreator linkCreator = context.getHstLinkCreator();
        Session session = context.getSession();
        String repoPath = context.getSiteContentBaseBean().getPath();
        NodeIterator resultIterator = getPublishedNodesUnderPath(session, repoPath);
        Mount mount = context.getResolvedMount().getMount();
        Urlset urlset = new Urlset();
        while (resultIterator.hasNext()) {
            Node child = resultIterator.nextNode();
            String path = linkCreator.create(child, mount).toUrlForm(context, true);
            Url url = url(path, child);
            urlset.getUrls().add(url);
        }
        return urlset;
    }

    private static Url url(String path, Node node) throws RepositoryException {
        Url url = new Url();
        url.setLastmod(getLastMode(node));
        url.setLoc(path);
        return url;
    }

    private static Calendar getLastMode(Node node) throws RepositoryException {
        return node.getProperty("hippostdpubwf:lastModificationDate").getDate();
    }

    private static NodeIterator getPublishedNodesUnderPath(Session session, String path) throws RepositoryException {
        String xpath = xpath(path);
        Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
        query.setLimit(MAX_ITEMS_PER_SITEMAP);
        QueryResult result = query.execute();
        return result.getNodes();
    }

    private static String xpath(String contentPath) {
        return String.format(
                "/jcr:root%s//element(*, publishing:base)" +
                "[hippostd:state = 'published']" +
                "[hippostd:stateSummary = 'live']",
                contentPath);
    }
}