package scot.mygov.publishing.channelevents;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.pagecomposer.jaxrs.api.PageCreateContext;
import org.hippoecm.hst.pagecomposer.jaxrs.api.PageCreateEvent;
import org.hippoecm.hst.pagecomposer.jaxrs.services.exceptions.ClientException;
import org.junit.Test;

import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewPageSlugValidationListenerTest {

    NewPageSlugValidationListener sut = new NewPageSlugValidationListener();

    @Test
    public void onPageCreateEventDoesNothingIfEventAlreadyHasException() throws Exception {
        // ARRANGE
        PageCreateEvent event = mock(PageCreateEvent.class);
        when(event.getException()).thenReturn(new RuntimeException("already failed"));

        // ACT
        sut.onPageCreateEvent(event);

        // ASSERT
        verify(event, never()).getPageActionContext();
    }

    @Test
    public void onPageCreateEventDoesNothingIfNoNewSiteMapItemNode() {
        // ARRANGE
        PageCreateEvent event = mock(PageCreateEvent.class);
        PageCreateContext context = mock(PageCreateContext.class);
        when(event.getPageActionContext()).thenReturn(context);
        when(context.getNewSiteMapItemNode()).thenReturn(null);

        // ACT
        sut.onPageCreateEvent(event);

        // ASSERT
        verify(event, never()).setException(any());
    }

    @Test
    public void onPageCreateEventSetsExceptionWhenSlugClashes() throws Exception {
        // ARRANGE
        PageCreateEvent event = mock(PageCreateEvent.class);
        PageCreateContext context = mock(PageCreateContext.class);
        Node newSiteMapItemNode = mock(Node.class);
        Mount mount = mock(Mount.class);
        HstRequestContext requestContext = mock(HstRequestContext.class);
        Session session = sessionWithClashAt("/content/documents/foodcommission/browse/who-we-are");

        when(event.getPageActionContext()).thenReturn(context);
        when(context.getNewSiteMapItemNode()).thenReturn(newSiteMapItemNode);
        when(newSiteMapItemNode.getName()).thenReturn("who-we-are");
        when(context.getEditingMount()).thenReturn(mount);
        when(mount.getContentPath()).thenReturn("/content/documents/foodcommission");
        when(context.getRequestContext()).thenReturn(requestContext);
        when(requestContext.getSession()).thenReturn(session);

        // ACT
        sut.onPageCreateEvent(event);

        // ASSERT
        verify(event).setException(any(ClientException.class));
    }

    @Test
    public void onPageCreateEventDoesNotSetExceptionWhenNoClash() throws Exception {
        // ARRANGE
        PageCreateEvent event = mock(PageCreateEvent.class);
        PageCreateContext context = mock(PageCreateContext.class);
        Node newSiteMapItemNode = mock(Node.class);
        Mount mount = mock(Mount.class);
        HstRequestContext requestContext = mock(HstRequestContext.class);
        Session session = sessionWithNoClash();

        when(event.getPageActionContext()).thenReturn(context);
        when(context.getNewSiteMapItemNode()).thenReturn(newSiteMapItemNode);
        when(newSiteMapItemNode.getName()).thenReturn("new-page");
        when(context.getEditingMount()).thenReturn(mount);
        when(mount.getContentPath()).thenReturn("/content/documents/foodcommission");
        when(context.getRequestContext()).thenReturn(requestContext);
        when(requestContext.getSession()).thenReturn(session);

        // ACT
        sut.onPageCreateEvent(event);

        // ASSERT
        verify(event, never()).setException(any());
    }

    @Test
    public void documentWithSlugReturnsNullWhenNoResults() throws Exception {
        // ARRANGE
        Session session = sessionWithNoClash();

        // ACT
        String actual = sut.documentWithSlug(session, "/content/documents/foodcommission", "new-page");

        // ASSERT
        assertNull(actual);
    }

    @Test
    public void documentWithSlugReturnsPathOfClashingDocument() throws Exception {
        // ARRANGE
        Session session = sessionWithClashAt("/content/documents/foodcommission/browse/who-we-are");

        // ACT
        String actual = sut.documentWithSlug(session, "/content/documents/foodcommission", "who-we-are");

        // ASSERT
        assertEquals("/content/documents/foodcommission/browse/who-we-are", actual);
    }

    Session sessionWithNoClash() throws Exception {
        return sessionWithQueryResult(false, null);
    }

    Session sessionWithClashAt(String path) throws Exception {
        return sessionWithQueryResult(true, path);
    }

    Session sessionWithQueryResult(boolean hasResult, String resultPath) throws Exception {
        Session session = mock(Session.class);
        Workspace workspace = mock(Workspace.class);
        QueryManager queryManager = mock(QueryManager.class);
        Query query = mock(Query.class);
        QueryResult queryResult = mock(QueryResult.class);
        NodeIterator nodeIterator = mock(NodeIterator.class);

        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(any(), eq(Query.XPATH))).thenReturn(query);
        when(query.execute()).thenReturn(queryResult);
        when(queryResult.getNodes()).thenReturn(nodeIterator);
        when(nodeIterator.hasNext()).thenReturn(hasResult);

        if (hasResult) {
            Node resultNode = mock(Node.class);
            when(resultNode.getPath()).thenReturn(resultPath);
            when(nodeIterator.nextNode()).thenReturn(resultNode);
        }

        return session;
    }
}
