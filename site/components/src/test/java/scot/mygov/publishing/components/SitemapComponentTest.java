package scot.mygov.publishing.components;

import org.apache.commons.lang.NotImplementedException;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedMount;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.onehippo.forge.sitemap.components.model.Urlset;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 24/09/2019.
 */
public class SitemapComponentTest {

    @Test(expected = HstComponentException.class)
    public void repoExceptionRethrownAsHstComponentException() throws Exception {

        // ARRANGE
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        HippoBean baseBean = mock(HippoBean.class);
        Session session = mock(Session.class);
        Workspace workspace = mock(Workspace.class);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getSiteContentBaseBean()).thenReturn(baseBean);
        when(context.getSession()).thenReturn(session);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenThrow(new RepositoryException("arg"));

        // ACT
        SitemapComponent.generateSitemap(request);

        // ASSERT - expect an exception
    }

    @Test
    public void generatesCorrectSitemap() throws Exception {

        // ARRANGE
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        HstLinkCreator linkCreator = mock(HstLinkCreator.class);
        HippoBean baseBean = mock(HippoBean.class);
        Workspace workspace = mock(Workspace.class);
        Session session = mock(Session.class);
        QueryManager queryManager = mock(QueryManager.class);
        Query query = mock(Query.class);
        QueryResult result = mock(QueryResult.class);
        ResolvedMount resolvedMount = mock(ResolvedMount.class);
        Mount mount = mock(Mount.class);

        when(request.getRequestContext()).thenReturn(context);
        when(context.getHstLinkCreator()).thenReturn(linkCreator);
        when(context.getSiteContentBaseBean()).thenReturn(baseBean);
        when(context.getResolvedMount()).thenReturn(resolvedMount);
        when(context.getSession()).thenReturn(session);
        when(resolvedMount.getMount()).thenReturn(mount);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(any(), any())).thenReturn(query);
        when(query.execute()).thenReturn(result);

        List<Node> nodes = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Node one = node(calendar);
        Collections.addAll(nodes, one);
        HstLink expectedLink = mock(HstLink.class);
        when(expectedLink.toUrlForm(context, true)).thenReturn("expectedurl");
        when(linkCreator.create(one, mount)).thenReturn(expectedLink);


        // linkCreator.create(child, mount).toUrlForm(context, true);
        when(result.getNodes()).thenReturn(iterator(nodes));

        // ACT
        Urlset actual = SitemapComponent.generateSitemap(request);

        // ASSERT
        assertEquals(1, actual.getUrls().size());
        assertEquals(actual.getUrls().get(0).getLoc(), "expectedurl");
        assertEquals(actual.getUrls().get(0).getLastmod(), calendar);

    }

    Node node(Calendar date) throws RepositoryException {
        Node node = mock(Node.class);
        Property property = mock(Property.class);
        when(property.getDate()).thenReturn(date);
        when(node.getProperty("hippostdpubwf:lastModificationDate")).thenReturn(property);
        return node;
    }

    public static NodeIterator iterator(Collection<Node> nodes) {

        Iterator<Node> it = nodes.iterator();

        return new NodeIterator() {

            public boolean hasNext() {
                return  it.hasNext();
            }

            public Node nextNode() {
                return (Node) it.next();
            }

            public void skip(long skipNum) {
                throw new NotImplementedException();
            }

            public long getSize() {
                return nodes.size();
            }

            public long getPosition() {
                throw new NotImplementedException();
            }

            public NodeIterator next() {
                throw new NotImplementedException();
            }
        };
    };
}
