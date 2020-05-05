package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedMount;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import scot.mygov.publishing.TestUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.*;

public class URLAliasComponentTest {
    @Test
    public void noRedirectSentIfNonePresent() throws RepositoryException {
        URLAliasComponent sut = new URLAliasComponent();
        sut.redirector = mock(URLAliasComponent.Redirector.class);
        HstRequest request = hstRequest(Collections.emptyList());
        HstResponse respone = mock(HstResponse.class);
        sut.doBeforeRender(request, respone);
        verify(sut.redirector, never()).redirect(any(), any(), any());
    }

    @Test
    public void redirectrSentIfPresent() throws RepositoryException {
        URLAliasComponent sut = new URLAliasComponent();
        sut.redirector = mock(URLAliasComponent.Redirector.class);
        Node node = mock(Node.class);
        HstRequest request = hstRequest(Collections.singletonList(node));
        HstResponse respone = mock(HstResponse.class);
        sut.doBeforeRender(request, respone);
        verify(sut.redirector).redirect(any(), any(), any());
    }

    @Test
    public void noRedirectSendForRepoException() throws RepositoryException {
        URLAliasComponent sut = new URLAliasComponent();
        sut.redirector = mock(URLAliasComponent.Redirector.class);
        Node node = mock(Node.class);
        HstRequest request = hstRequestWithexceptionThrowingSession();
        HstResponse respone = mock(HstResponse.class);
        sut.doBeforeRender(request, respone);
        verify(sut.redirector, never()).redirect(any(), any(), any());
    }

    @Test
    public void targetStateReturnsRightValues() {
        URLAliasComponent sut = new URLAliasComponent();

        Mount liveMount = mountWithType("live");
        assertEquals("published", sut.targetState(liveMount));

        Mount previewMount = mountWithType("preview");
        assertEquals("draft", sut.targetState(previewMount));
    }

    Mount mountWithType(String type) {
        Mount mount = mock(Mount.class);
        when(mount.getType()).thenReturn(type);
        return mount;
    }

    HstRequest hstRequest(List<Node> nodes) throws RepositoryException {
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        Session session = mock(Session.class);
        Workspace workspace = mock(Workspace.class);
        QueryManager queryManager = mock(QueryManager.class);
        Query query = mock(Query.class);
        QueryResult queryResult = mock(QueryResult.class);
        Mount mount = mountWithType("live");
        ResolvedMount resolvedMount = mock(ResolvedMount.class);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getSession()).thenReturn(session);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(anyString(), anyString())).thenReturn(query);
        when(query.execute()).thenReturn(queryResult);
        when(queryResult.getNodes()).thenReturn(TestUtil.iterator(nodes));
        when(context.getResolvedMount()).thenReturn(resolvedMount);
        when(resolvedMount.getMount()).thenReturn(mount);
        return request;
    }

    HstRequest hstRequestWithexceptionThrowingSession() throws RepositoryException {
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        Session session = mock(Session.class);
        Workspace workspace = mock(Workspace.class);
        QueryManager queryManager = mock(QueryManager.class);
        Query query = mock(Query.class);
        Mount mount = mountWithType("live");
        ResolvedMount resolvedMount = mock(ResolvedMount.class);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getSession()).thenReturn(session);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(anyString(), anyString())).thenReturn(query);
        when(query.execute()).thenThrow(new RepositoryException("arg"));
        when(context.getResolvedMount()).thenReturn(resolvedMount);
        when(resolvedMount.getMount()).thenReturn(mount);
        return request;
    }
}
