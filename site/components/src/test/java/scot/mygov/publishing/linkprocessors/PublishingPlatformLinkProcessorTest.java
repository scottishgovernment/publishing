package scot.mygov.publishing.linkprocessors;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.configuration.hosting.VirtualHost;
import org.hippoecm.hst.core.linking.HstLink;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import scot.mygov.publishing.TestUtil;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PublishingPlatformLinkProcessorTest {

    PublishingPlatformLinkProcessor sut = new PublishingPlatformLinkProcessor();

    @Test
    public void preProcessDoesNotChangeLocalLink() {
        HstLink link = localhostLink();
        HstLink actual = sut.preProcess(link);
        assertSame(link, actual);
    }

    @Test
    public void postProcessDoesNotChangeLocalLink() {
        HstLink link = localhostLink();
        HstLink actual = sut.postProcess(link);
        assertSame(link, actual);
    }

    @Test
    public void preProcessDoesNotChangeBinaryLink() {
        HstLink link = binariesLink();
        HstLink actual = sut.preProcess(link);
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
    public void postProcessReturnsUnchangedLinkUnpublishedIndex() throws RepositoryException {
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
        verify(link).setPath("/slug");
    }

    @Test
    public void postProcessReturnsLinkForGuidePage() throws RepositoryException {
        Session session = sessionWithGuidePage();
        sut.sessionSource = () -> session;
        HstLink link = link("link");
        sut.postProcess(link);
        verify(link).setPath("/guideslug/pagename");
    }

    @Test
    public void postProcessReturnsLinkForGuide() throws RepositoryException {
        Session session = sessionWithGuide();
        sut.sessionSource = () -> session;
        HstLink link = link("link");
        sut.postProcess(link);
        verify(link).setPath("/guideslug");
    }

    @Test
    public void postProcessReturnsLinkForSmartAnswer() throws RepositoryException {
        Session session = sessionWithSmartAnswer();
        sut.sessionSource = () -> session;
        HstLink link = link("link");
        sut.postProcess(link);
        verify(link).setPath("/smartanswerslug");
    }

    @Test
    public void preProcessIgnoresUrlsWithExtensions() {
        HstLink link = link("/path/with-extension.jpeg");
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

    HstLink localhostLink() {
        HstLink link = mock(HstLink.class);
        Mount mount = mock(Mount.class);
        VirtualHost virtualHost = mock(VirtualHost.class);
        when(link.getMount()).thenReturn(mount);
        when(mount.getVirtualHost()).thenReturn(virtualHost);
        when(virtualHost.getName()).thenReturn("localhost");
        return link;
    }

    HstLink link(String path) {
        HstLink link = mock(HstLink.class);
        when(link.getPath()).thenReturn(path);
        Mount mount = mock(Mount.class);
        VirtualHost virtualHost = mock(VirtualHost.class);
        when(virtualHost.getName()).thenReturn("");
        when(mount.getVirtualHost()).thenReturn(virtualHost);
        when(link.getMount()).thenReturn(mount);
        when(link.getPathElements()).thenReturn(path.split("/"));
        return link;
    }

    HstLink binariesLink() {
        return link("/binaries/thisisabinary");
    }

    HstLink folderLink() {
        HstLink link = mock(HstLink.class);
        Mount mount = mock(Mount.class);
        VirtualHost virtualHost = mock(VirtualHost.class);
        when(virtualHost.getName()).thenReturn("");
        when(mount.getVirtualHost()).thenReturn(virtualHost);
        when(mount.getContentPath()).thenReturn("contenPath");
        when(link.getPath()).thenReturn("/folder/");
        when(link.getMount()).thenReturn(mount);
        return link;
    }

    Session sessionWithFolder() throws RepositoryException {
        Session session = mock(Session.class);
        Node folder = mock(Node.class);
        when(folder.isNodeType("hippostd:folder")).thenReturn(true);
        when(session.nodeExists(any())).thenReturn(true);
        when(session.getNode(any())).thenReturn(folder);
        return session;
    }

    Session sessionWithArticle() throws RepositoryException {
        Session session = mock(Session.class);
        Node handle = mock(Node.class);
        Node article = mock(Node.class);
        when(handle.isNodeType("hippostd:folder")).thenReturn(false);
        when(session.nodeExists(any())).thenReturn(true);
        when(session.getNode(any())).thenReturn(handle);
        when(handle.getNode(any())).thenReturn(article);
        Property slug = mock(Property.class);
        when(slug.getString()).thenReturn("slug");
        when(article.getProperty("publishing:slug")).thenReturn(slug);
        when(article.hasProperty("publishing:slug")).thenReturn(true);
        return session;
    }

    Session sessionWithGuidePage() throws RepositoryException {
        Session session = mock(Session.class);
        Node guidepagehandle = mock(Node.class);
        Node guidepage = mock(Node.class);

        Node guidepagehandle2 = mock(Node.class);
        Node guidepage2 = mock(Node.class);

        Node guidehandle = mock(Node.class);
        Node guide = mock(Node.class);

        Node guidefolder = mock(Node.class);
        when(guidepage.getName()).thenReturn("pagename");
        when(guidepage.getParent()).thenReturn(guidehandle);

        when(guidepage2.getName()).thenReturn("pagename2");
        when(guidepage2.getParent()).thenReturn(guidepagehandle2);

        when(guidehandle.getParent()).thenReturn(guidefolder);

        when(guidefolder.getNode("index")).thenReturn(guidehandle);
        when(guidehandle.getNode("index")).thenReturn(guide);

        when(guidepagehandle.isNodeType("hippostd:folder")).thenReturn(false);
        when(guidepage.isNodeType("publishing:guidepage")).thenReturn(true);
        when(guidepagehandle.getNodes()).thenReturn(TestUtil.iterator(Collections.singletonList(guidepage)));

        when(guidepagehandle2.isNodeType("hippostd:folder")).thenReturn(false);
        when(guidepage2.isNodeType("publishing:guidepage")).thenReturn(true);
        when(guidepagehandle2.getNodes()).thenReturn(TestUtil.iterator(Collections.singletonList(guidepage2)));

        List<Node> folderChilter = new ArrayList<>();
        Collections.addAll(folderChilter, guidepagehandle, guidepagehandle2);
        when(guidefolder.getNodes()).thenReturn(TestUtil.iterator(folderChilter));
        when(session.nodeExists(any())).thenReturn(true);
        when(session.getNode(any())).thenReturn(guidepagehandle);
        when(guidepagehandle.getNode(any())).thenReturn(guidepage);
        Property slug = mock(Property.class);
        when(slug.getString()).thenReturn("guideslug");
        when(guide.getProperty("publishing:slug")).thenReturn(slug);
        when(guide.hasProperty("publishing:slug")).thenReturn(true);
        return session;
    }

    Session sessionWithGuide() throws RepositoryException {
        Session session = mock(Session.class);
        Node guidepagehandle = mock(Node.class);
        Node guidepage = mock(Node.class);

        Node guidepagehandle2 = mock(Node.class);
        Node guidepage2 = mock(Node.class);

        Node guidehandle = mock(Node.class);
        Node guide = mock(Node.class);

        Node guidefolder = mock(Node.class);
        when(guidefolder.isNodeType("hippostd:folder")).thenReturn(true);
        when(guidepage.getName()).thenReturn("pagename");
        when(guidepage.getParent()).thenReturn(guidehandle);

        when(guidepage2.getName()).thenReturn("pagename2");
        when(guidepage2.getParent()).thenReturn(guidepagehandle2);

        when(guidehandle.getParent()).thenReturn(guidefolder);

        when(guidefolder.hasNode("index")).thenReturn(true);
        when(guidehandle.getNode("index")).thenReturn(guide);


        when(guidefolder.getNode("index")).thenReturn(guidehandle);
        when(guidehandle.hasNode("index")).thenReturn(true);
        when(guidehandle.getNode("index")).thenReturn(guide);
        when(guide.isNodeType("publishing:guide")).thenReturn(true);

        when(guidepagehandle.isNodeType("hippostd:folder")).thenReturn(false);
        when(guidepage.isNodeType("publishing:guidepage")).thenReturn(true);
        when(guidepagehandle.getNodes()).thenReturn(TestUtil.iterator(Collections.singletonList(guidepage)));

        when(guidepagehandle2.isNodeType("hippostd:folder")).thenReturn(false);
        when(guidepage2.isNodeType("publishing:guidepage")).thenReturn(true);
        when(guidepagehandle2.getNodes()).thenReturn(TestUtil.iterator(Collections.singletonList(guidepage2)));

        List<Node> folderChilter = new ArrayList<>();
        Collections.addAll(folderChilter, guidepagehandle, guidepagehandle2);
        when(guidefolder.getNodes()).thenReturn(TestUtil.iterator(folderChilter));
        when(session.nodeExists(any())).thenReturn(true);

        when(session.getNode(any())).thenReturn(guidefolder);
        when(guidepagehandle.getNode(any())).thenReturn(guidepage);
        Property slug = mock(Property.class);
        when(slug.getString()).thenReturn("guideslug");
        when(guide.getProperty("publishing:slug")).thenReturn(slug);
        when(guide.hasProperty("publishing:slug")).thenReturn(true);
        return session;
    }

    Session sessionWithSmartAnswer() throws RepositoryException {
        Session session = mock(Session.class);
        Node handle = mock(Node.class);
        Node smartanswer = mock(Node.class);

        Node folder = mock(Node.class);
        when(folder.isNodeType("hippostd:folder")).thenReturn(true);
        when(handle.getParent()).thenReturn(folder);

        when(folder.hasNode("index")).thenReturn(true);
        when(handle.getNode("index")).thenReturn(smartanswer);
        when(folder.getNode("index")).thenReturn(handle);
        when(handle.hasNode("index")).thenReturn(true);
        when(handle.getNode("index")).thenReturn(smartanswer);
        when(smartanswer.isNodeType("publishing:guide")).thenReturn(false);
        when(smartanswer.isNodeType("publishing:smartanswer")).thenReturn(true);

        when(session.nodeExists(any())).thenReturn(true);
        when(session.getNode(any())).thenReturn(folder);
        Property slug = mock(Property.class);
        when(slug.getString()).thenReturn("smartanswerslug");
        when(smartanswer.getProperty("publishing:slug")).thenReturn(slug);
        when(smartanswer.hasProperty("publishing:slug")).thenReturn(true);
        return session;
    }

    Session exceptionThrowingSession() throws RepositoryException {
        Session session = mock(Session.class);
        when(session.nodeExists(any())).thenThrow(new RepositoryException("arg"));
        when(session.getNode(any())).thenThrow(new RepositoryException("arg"));
        return session;
    }

    Session sessionWithLookupButNoPath() throws RepositoryException {
        Session session = mock(Session.class);
        Node node = mock(Node.class);
        when(session.nodeExists(any())).thenReturn(true);
        when(session.getNode(any())).thenReturn(node);
        when(node.hasProperty("publishing:path")).thenReturn(false);
        return session;
    }

    Session sessionWithLookup() throws RepositoryException {
        Session session = mock(Session.class);
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
        Session session = mock(Session.class);
        when(session.nodeExists(any())).thenReturn(false);
        return session;
    }



}
