package scot.mygov.publishing.linkprocessors;

import org.apache.jackrabbit.util.Text;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.linking.HstLink;
import org.junit.Assert;
import org.junit.Test;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FooterLinkProcessorTest {

    @Test
    public void postProcessIgnoresNonFooterLinks() {
        // ARRANGE
        FooterLinkProcessor sut = new FooterLinkProcessor();
        HstLink input = link(new String [] {"category", "article"}, null);

        // ACT
        sut.doPostProcess(input);

        // ASSERT
        verify(input, never()).setPath(any());
    }

    @Test
    public void postProcessRewritesFooterLinks() {
        // ARRANGE
        FooterLinkProcessor sut = new FooterLinkProcessor();
        HstLink input = link(new String [] {"footer", "about"}, null);

        // ACT
        sut.doPostProcess(input);

        // ASSERT
        verify(input).setPath(eq("about"));
    }

    @Test
    public void doPreProcessIgnoresNonSinglePaths() {
        // ARRANGE
        FooterLinkProcessor sut = new FooterLinkProcessor();
        HstLink input = link(new String [] {"one", "two", "three"}, null);

        // ACT
        HstLink actual = sut.doPreProcess(input);

        // ASSERT - the seme link is returned and no path has been set
        assertSame(input, actual);
        verify(input, never()).setPath(any());
    }

    @Test
    public void doPreProcessReturnsOriginalLinkIfNoFiiterItemExists() {
        // ARRANGE
        FooterLinkProcessor sut = new FooterLinkProcessor();
        HstLink input = link(new String [] {"cookies"}, null);
        Session session = mock(Session.class);
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
        FooterLinkProcessor sut = new FooterLinkProcessor();
        HstLink input = link(new String [] {"cookies"}, "/content/documents/mysite");
        Session session = mock(Session.class);
        Node cookiesHandle = mock(Node.class);
        when(cookiesHandle.getName()).thenReturn("cookies");
        when(session.nodeExists("/content/documents/mysite/footer/cookies")).thenThrow(new RepositoryException("arg"));
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
        FooterLinkProcessor sut = new FooterLinkProcessor();
        HstLink input = link(new String [] {"cookies"}, "/content/documents/mysite");
        Session session = mock(Session.class);
        Node cookiesHandle = mock(Node.class);
        when(cookiesHandle.getName()).thenReturn("cookies");
        when(session.nodeExists("/content/documents/mysite/footer/cookies")).thenReturn(true);
        when(session.getNode("/content/documents/mysite/footer/cookies")).thenReturn(cookiesHandle);
        sut.sessionSource = link -> session;

        // ACT
        HstLink actual = sut.doPreProcess(input);

        // ASSERT - the seme link is returned and no path has been set
        assertSame(input, actual);
        verify(input).setPath("footer/cookies");
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
