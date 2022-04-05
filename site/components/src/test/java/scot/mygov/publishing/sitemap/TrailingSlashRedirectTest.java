package scot.mygov.publishing.sitemap;

import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class TrailingSlashRedirectTest {

    @Test
    public void processRemovesTrailingSlash() {
        // ARRANGE
        TrailingSlashRedirect sut = new TrailingSlashRedirect();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ResolvedSiteMapItem siteMapItem = mock(ResolvedSiteMapItem.class);
        when(request.getPathTranslated()).thenReturn("path/");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://www.site.com/path/"));

        // ACT
        ResolvedSiteMapItem actual = sut.process(siteMapItem, request, response);

        // ASSERT
        assertNull(actual);
        verify(response).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(response).setHeader("Location", "http://www.site.com/path");
    }

    @Test
    public void redirectTargetRemovesTrailingSlashBeforeQueryString() {
        // ARRANGE
        TrailingSlashRedirect sut = new TrailingSlashRedirect();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ResolvedSiteMapItem siteMapItem = mock(ResolvedSiteMapItem.class);
        when(request.getPathTranslated()).thenReturn("path/");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://www.site.com/path/"));
        when(request.getQueryString()).thenReturn("param=value");

        // ACT
        ResolvedSiteMapItem actual = sut.process(siteMapItem, request, response);

        // ASSERT
        assertNull(actual);
        verify(response).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(response).setHeader("Location", "http://www.site.com/path?param=value");
    }

    @Test
    public void processIgnoresPathWithNoTrailingSlashes() {
        // ARRANGE
        TrailingSlashRedirect sut = new TrailingSlashRedirect();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ResolvedSiteMapItem siteMapItem = mock(ResolvedSiteMapItem.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://www.site.com/path"));
        when(request.getPathTranslated()).thenReturn("path");

        // ACT
        ResolvedSiteMapItem actual = sut.process(siteMapItem, request, response);

        // ASSERT
        assertSame(actual, siteMapItem);
        verify(response, never()).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(response, never()).setHeader("Location", "http://www.site.com/path");
    }

    @Test
    public void processDoesNotRedirectRequestsWithExtension() {
        // ARRANGE
        TrailingSlashRedirect sut = new TrailingSlashRedirect();
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ResolvedSiteMapItem siteMapItem = mock(ResolvedSiteMapItem.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://www.site.com/path/file.pdf"));
        when(request.getPathTranslated()).thenReturn("path/file.pdf");

        // ACT
        ResolvedSiteMapItem actual = sut.process(siteMapItem, request, response);

        // ASSERT
        assertSame(actual, siteMapItem);
        verify(response, never()).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(response, never()).setHeader("Location", "http://www.site.com/path/file.pdf");
    }

}
