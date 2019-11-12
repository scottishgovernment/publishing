package scot.mygov.publishing.linkprocessors;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.linking.HstLink;
import org.junit.Test;
import scot.mygov.publishing.TestUtil;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SiteFurnitureLinkProcessorTest {

    @Test
    public void postProcessIgnoresNonFooterLinks() {
        // ARRANGE
        SiteFurnitureLinkProcessor sut = new SiteFurnitureLinkProcessor();
        HstLink input = link(new String [] {"category", "article"}, null);

        // ACT
        sut.doPostProcess(input);

        // ASSERT
        verify(input, never()).setPath(any());
    }

    @Test
    public void postProcessRewritesFooterLinks() {
        // ARRANGE
        SiteFurnitureLinkProcessor sut = new SiteFurnitureLinkProcessor();
        HstLink input = link(new String [] {"site-furniture", "footer", "about"}, null);

        // ACT
        sut.doPostProcess(input);

        // ASSERT
        verify(input).setPath(eq("about"));
    }

    @Test
    public void postProcessRewritesNonFooterSiteFurnitureLinks() {
        // ARRANGE
        SiteFurnitureLinkProcessor sut = new SiteFurnitureLinkProcessor();
        HstLink input = link(new String [] {"site-furniture", "cookies"}, null);

        // ACT
        sut.doPostProcess(input);

        // ASSERT
        verify(input).setPath(eq("cookies"));
    }

    @Test
    public void doPreProcessIgnoresNonSinglePaths() {
        // ARRANGE
        SiteFurnitureLinkProcessor sut = new SiteFurnitureLinkProcessor();
        HstLink input = link(new String [] {"one", "two", "three"}, null);

        // ACT
        HstLink actual = sut.doPreProcess(input);

        // ASSERT - the seme link is returned and no path has been set
        assertSame(input, actual);
        verify(input, never()).setPath(any());
    }

    @Test
    public void doPreProcessReturnsOriginalLinkIfNoItemExists() throws RepositoryException {
        // ARRANGE
        SiteFurnitureLinkProcessor sut = new SiteFurnitureLinkProcessor();
        HstLink input = link(new String [] {"cookies"}, null);
        Session session = sessionWithQueryResults();
        sut.sessionSource = link -> session;

        // ACT
        HstLink actual = sut.doPreProcess(input);

        // ASSERT - the seme link is returned and no path has been set
        assertSame(input, actual);
        verify(input, never()).setPath(any());
    }

    @Test
    public void doPreProcessReturnsOriginalLinkIfRepoExceptionThrown() throws RepositoryException {
        // ARRANGE
        SiteFurnitureLinkProcessor sut = new SiteFurnitureLinkProcessor();
        HstLink input = link(new String [] {"cookies"}, "/content/documents/mysite");
        Session session = exceptionThrowingSession();
        sut.sessionSource = link -> session;

        // ACT
        HstLink actual = sut.doPreProcess(input);

        // ASSERT - the seme link is returned and no path has been set
        assertSame(input, actual);
        verify(input, never()).setPath(any());
    }

    @Test
    public void doPreProcessSetsLinkIfFoundBySlug() throws RepositoryException {
        // ARRANGE
        SiteFurnitureLinkProcessor sut = new SiteFurnitureLinkProcessor();
        HstLink input = link(new String [] {"cookies"}, "/content/documents/mysite");
        Node cookiesHandle = mock(Node.class);
        Node cookiesPage = mock(Node.class);
        when(cookiesPage.getParent()).thenReturn(cookiesHandle);
        when(cookiesHandle.getPath()).thenReturn("/content/documents/trading-nation/site-furniture/footer/cookies");
        //when(cookiesHandle.getName()).thenReturn("cookies");
        Session session = sessionWithQueryResults(cookiesPage);

        sut.sessionSource = link -> session;

        // ACT
        HstLink actual = sut.doPreProcess(input);

        // ASSERT - the seme link is returned and no path has been set
        assertSame(input, actual);
        verify(input).setPath("site-furniture/footer/cookies");
    }

    @Test
    public void doPreProcessSetsLinkIfFoundBySlugButNotInSiteFurnitureFolder() throws RepositoryException {
        // ARRANGE
        SiteFurnitureLinkProcessor sut = new SiteFurnitureLinkProcessor();
        HstLink input = link(new String [] {"cookies"}, "/content/documents/mysite");
        Node cookiesHandle = mock(Node.class);
        Node cookiesPage = mock(Node.class);
        when(cookiesPage.getParent()).thenReturn(cookiesHandle);
        when(cookiesHandle.getPath()).thenReturn("/content/documents/trading-nation/category/cookies");
        //when(cookiesHandle.getName()).thenReturn("cookies");
        Session session = sessionWithQueryResults(cookiesPage);

        sut.sessionSource = link -> session;

        // ACT
        HstLink actual = sut.doPreProcess(input);

        // ASSERT - the seme link is returned and no path has been set
        assertSame(input, actual);
        verify(input, never()).setPath(any());
    }

    Session sessionWithQueryResults(Node ...nodes) throws RepositoryException {
        Session session = mock(Session.class);
        Workspace workspace = mock(Workspace.class);
        QueryManager queryManager = mock(QueryManager.class);
        Query query = mock(Query.class);
        QueryResult queryResult = mock(QueryResult.class);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(any(), any())).thenReturn(query);
        when(query.execute()).thenReturn(queryResult);
        when(queryResult.getNodes()).thenReturn(TestUtil.iterator(nodes));
        return session;
    }

    Session exceptionThrowingSession() throws RepositoryException {
        Session session = mock(Session.class);
        Workspace workspace = mock(Workspace.class);
        QueryManager queryManager = mock(QueryManager.class);
        Query query = mock(Query.class);
        QueryResult queryResult = mock(QueryResult.class);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(any(), any())).thenReturn(query);
        when(query.execute()).thenReturn(queryResult);
        when(queryResult.getNodes()).thenThrow(new RepositoryException(""));
        return session;
    }

    HstLink link(String [] elements, String contentPath) {
        HstLink link = mock(HstLink.class);
        Mount mount = mock(Mount.class);
        when(mount.getContentPath()).thenReturn(contentPath);
        when(link.getPathElements()).thenReturn(elements);
        when(link.getMount()).thenReturn(mount);
        return link;
    }

}
