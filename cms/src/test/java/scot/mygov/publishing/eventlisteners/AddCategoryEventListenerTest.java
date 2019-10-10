package scot.mygov.publishing.eventlisteners;

import org.junit.Test;
import org.mockito.Mockito;
import org.onehippo.cms7.event.HippoEvent;
import scot.mygov.publishing.TestUtil;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

public class AddCategoryEventListenerTest {

    @Test
    public void repoExceptionSwallowed() throws Exception {
        // ARRANGE
        Session session = mock(Session.class);
        when(session.getNode(any())).thenThrow(new RepositoryException());
        AddCategoryEventListener sut = new AddCategoryEventListener(session);
        HippoEvent event = new HippoEvent("app");

        // ACT
        sut.handleEvent(event);

        // ASSERT - no exception is thrown
    }

    @Test
    public void ignoresNonAddEvent() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        Node folder = folderNode();
        when(session.getNode("path")).thenReturn(folder);
        AddCategoryEventListener sut = new AddCategoryEventListener(session);
        HippoEvent event = eventWithAction("subtract").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        Mockito.verify(folder, never()).setProperty(eq("hippostd:foldertype"), any(String[].class));
    }

    @Test
    public void ignoresAddNonFolderEvent() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        Node folder = nonFolderNode();
        when(session.getNode("path")).thenReturn(folder);
        AddCategoryEventListener sut = new AddCategoryEventListener(session);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        Mockito.verify(folder, never()).setProperty(eq("hippostd:foldertype"), any(String[].class));
    }

    @Test
    public void setsActionsForCategoryLessThan10Deep() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        Node folder = folderNode();
        when(folder.getDepth()).thenReturn(5);
        when(session.getNode("path")).thenReturn(folder);
        AddCategoryEventListener sut = new AddCategoryEventListener(session);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        Mockito.verify(folder).setProperty("hippostd:foldertype", allActions());
    }

    @Test
    public void setsActionsForCategor9Deep() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        Node folder = folderNode();
        when(folder.getDepth()).thenReturn(12); // the + 3 for the part of the path before the site.
        when(session.getNode("path")).thenReturn(folder);
        AddCategoryEventListener sut = new AddCategoryEventListener(session);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        Mockito.verify(folder).setProperty("hippostd:foldertype", allActions());
    }

    @Test
    public void setsActionsForCategor10Deep() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        Node folder = folderNode();
        when(folder.getDepth()).thenReturn(13); // the + 3 for the part of the path before the site.
        when(session.getNode("path")).thenReturn(folder);
        AddCategoryEventListener sut = new AddCategoryEventListener(session);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        Mockito.verify(folder).setProperty("hippostd:foldertype", actionsWithoutAddCategory());
    }

    @Test
    public void setsActionsForCategoryMorethan10Deep() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        Node folder = folderNode();
        when(folder.getDepth()).thenReturn(14); // the + 3 for the part of the path before the site.
        when(session.getNode("path")).thenReturn(folder);

        AddCategoryEventListener sut = new AddCategoryEventListener(session);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        Mockito.verify(folder).setProperty("hippostd:foldertype", actionsWithoutAddCategory());
    }

    @Test
    public void setsNavigationStyleToListIfDeeperThan1() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        Node index = mock(Node.class);
        Node folder = folderNode(index);
        when(folder.getDepth()).thenReturn(10); // the + 3 for the part of the path before the site.
        when(session.getNode("path")).thenReturn(folder);

        AddCategoryEventListener sut = new AddCategoryEventListener(session);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        Mockito.verify(index).setProperty("publishing:navigationType", "list");
    }

    @Test
    public void setsNavigationStyleToListIf1Deep() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        Node index = mock(Node.class);
        Node folder = folderNode(index);
        when(folder.getDepth()).thenReturn(4); // the + 3 for the part of the path before the site.
        when(session.getNode("path")).thenReturn(folder);

        AddCategoryEventListener sut = new AddCategoryEventListener(session);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        Mockito.verify(index).setProperty("publishing:navigationType", "grid");
    }

    String [] allActions() {
        return new String [] {"new-publishing-article", "new-publishing-category"};
    }

    String [] actionsWithoutAddCategory() {
        return new String [] {"new-publishing-article"};
    }

    HippoEvent eventWithAction(String action) {
        HippoEvent event = new HippoEvent("app");
        event.action(action);
        return event;
    }

    Node folderNode(Node index) throws RepositoryException {
        Node folder = Mockito.mock(Node.class);
        Node indexHandle = mock(Node.class);
        when(folder.isNodeType("hippostd:folder")).thenReturn(true);
        when(indexHandle.getNodes()).thenReturn(TestUtil.iterator(singletonList(index)));
        when(folder.getNode("index")).thenReturn(indexHandle);
        return folder;
    }

    Node folderNode() throws RepositoryException {
        Node index = mock(Node.class);
        return folderNode(index);
    }

    Node nonFolderNode() throws RepositoryException {
        Node folder = Mockito.mock(Node.class);
        when(folder.isNodeType("hippostd:folder")).thenReturn(false);
        return folder;
    }

}
