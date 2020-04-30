package scot.mygov.publishing.linkprocessors;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.linking.HstLink;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PublishingPlatformLinkProcessorTest {

    PublishingPlatformLinkProcessor sut = new PublishingPlatformLinkProcessor();

    @Test
    public void postProcessIgnoresUrlsWithExtensions() {
        HstLink link = link("/path/with-extension.jpeg");
        HstLink actual = sut.postProcess(link);
        assertSame(link, actual);
    }

    @Test
    public void postProcessRepoExceptionReturnsUnchangedLink() throws RepositoryException {
        Session session = exceptionThrowingSession();
        sut.sessionSource = () -> session;
        HstLink link = link("/path/with-extension");
        HstLink actual = sut.postProcess(link);
        assertSame(link, actual);
    }

    @Test
    public void postProcessReturnsUnchangedLinkForFolders() throws RepositoryException {
        Session session = sessionWithFolder();
        sut.sessionSource = () -> session;
        HstLink link = folderLink();
        HstLink actual = sut.postProcess(link);
        assertSame(link, actual);
    }

    @Test
    public void postProcessReturnsSlugLink() throws RepositoryException {
        Session session = sessionWithArticle();
        sut.sessionSource = () -> session;
        HstLink link = link("link");
        HstLink actual = sut.postProcess(link);
        verify(link).setPath("slug");
    }

    @Test
    public void preProcessIgnoresUrlsWithExtensions() {
        HstLink link = link("/path/with-extension.jpeg");
        HstLink actual = sut.preProcess(link);
        assertSame(link, actual);
    }

    @Test
    public void preProcessIgnoresMultiElementPathsUrlsWithExtensions() {
        HstLink link = link("/path/with/folders");
        HstLink actual = sut.preProcess(link);
        assertSame(link, actual);
    }

    @Test
    public void preProcessRepoExceptionReturnsUnchangedLink() throws RepositoryException {
        Session session = exceptionThrowingSession();
        sut.sessionSource = () -> session;
        HstLink link = link("path");
        HstLink actual = sut.preProcess(link);
        assertSame(link, actual);
    }

    @Test
    public void preProcessReturnsUnchangedLinkIfNoLookupIsAvailable() throws RepositoryException {
        Session session = sessionWithNoAvailableLookup();
        sut.sessionSource = () -> session;
        HstLink link = link("path");
        HstLink actual = sut.preProcess(link);
        assertSame(link, actual);
    }

    @Test
    public void preProcessReturnsUnchangedLinkIfLookupHasNoPathProperty() throws RepositoryException {
        Session session = sessionWithLookupButNoPath();
        sut.sessionSource = () -> session;
        HstLink link = link("path");
        HstLink actual = sut.preProcess(link);
        assertSame(link, actual);
    }

    @Test
    public void preProcessReturnsSlugLinkIfLookupIsAvailable() throws RepositoryException {
        Session session = sessionWithLookup();
        sut.sessionSource = () -> session;
        HstLink link = link("path");
        HstLink actual = sut.preProcess(link);
        verify(link).setPath("looked-up-path");
    }

    HstLink link(String path) {
        HstLink link = mock(HstLink.class);
        when(link.getPath()).thenReturn(path);
        Mount mount = mock(Mount.class);
        when(link.getMount()).thenReturn(mount);
        when(link.getPathElements()).thenReturn(path.split("/"));
        return link;
    }

    HstLink folderLink() {
        HstLink link = mock(HstLink.class);
        Mount mount = mock(Mount.class);
        when(mount.getContentPath()).thenReturn("contenPath");
        when(link.getPath()).thenReturn("/folder/");
        when(link.getMount()).thenReturn(mount);
        return link;
    }

    Session sessionWithFolder() throws RepositoryException {
        Session session = Mockito.mock(Session.class);
        Node folder = Mockito.mock(Node.class);
        when(folder.isNodeType("hippostd:folder")).thenReturn(true);
        when(session.getNode(any())).thenReturn(folder);
        return session;
    }

    Session sessionWithArticle() throws RepositoryException {
        Session session = Mockito.mock(Session.class);
        Node handle = Mockito.mock(Node.class);
        Node article = Mockito.mock(Node.class);
        when(handle.isNodeType("hippostd:folder")).thenReturn(false);
        when(session.getNode(any())).thenReturn(handle);
        when(handle.getNode(any())).thenReturn(article);
        Property slug = mock(Property.class);
        when(slug.getString()).thenReturn("slug");
        when(article.getProperty("publishing:slug")).thenReturn(slug);
        return session;
    }

    Session exceptionThrowingSession() throws RepositoryException {
        Session session = Mockito.mock(Session.class);
        when(session.getNode(any())).thenThrow(new RepositoryException("arg"));
        return session;
    }

    Session sessionWithLookupButNoPath() throws RepositoryException {
        Session session = Mockito.mock(Session.class);
        Node node = mock(Node.class);
        when(session.nodeExists(any())).thenReturn(true);
        when(session.getNode(any())).thenReturn(node);
        when(node.hasProperty("publishing:path")).thenReturn(false);
        return session;
    }

    Session sessionWithLookup() throws RepositoryException {
        Session session = Mockito.mock(Session.class);
        Node node = mock(Node.class);
        when(session.nodeExists(any())).thenReturn(true);
        when(session.getNode(any())).thenReturn(node);
        when(node.hasProperty("publishing:path")).thenReturn(true);
        Property p = mock(Property.class);
        when(p.getString()).thenReturn("looked-up-path");
        when(node.getProperty("publishing:path")).thenReturn(p);
        return session;
    }

    Session sessionWithNoAvailableLookup() throws RepositoryException {
        Session session = Mockito.mock(Session.class);
        when(session.nodeExists(any())).thenReturn(false);
        return session;
    }



}
