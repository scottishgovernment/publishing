package scot.mygov.publishing.eventlisteners;

import org.junit.Test;
import org.onehippo.repository.events.HippoWorkflowEvent;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SlugMaintenanceListenerTest {

    @Test
    public void repoExceptionHandledCorrectly() throws RepositoryException {

        // ARRANGE
        Session session = exceptionThrowingSession();
        SlugMaintenanceListener sut = new SlugMaintenanceListener(session);

        // ACT
        sut.handleEvent(event("any", "subject"));

        // ASSERT
        verify(session).refresh(false);
    }


    @Test
    public void categoriesAreIgnored() throws RepositoryException {

        // ARRANGE
        Session session = sessionWithCategory();
        SlugMaintenanceListener sut = new SlugMaintenanceListener(session);
        sut.slugLookups = mock(SlugLookups.class);

        // ACT
        sut.handleEvent(event("any", "subject"));

        // ASSERT
        verify(sut.slugLookups, never()).updateLookup(any(), any());
        verify(sut.slugLookups, never()).removeLookup(any(), any());
    }

    @Test
    public void previewLookupUpdatedForCommitEditableInstance() throws RepositoryException {
        // ARRANGE
        Session session = sessionWithArticle();
        SlugMaintenanceListener sut = new SlugMaintenanceListener(session);
        sut.slugLookups = mock(SlugLookups.class);

        // ACT
        sut.handleEvent(event("commitEditableInstance", "subject"));

        // ASSERT
        verify(sut.slugLookups).updateLookup(any(), eq("preview"));
        verify(sut.slugLookups, never()).removeLookup(any(), any());
    }

    @Test
    public void liveLookupUpdatedForPublish() throws RepositoryException {
        // ARRANGE
        Session session = sessionWithArticle();
        SlugMaintenanceListener sut = new SlugMaintenanceListener(session);
        sut.slugLookups = mock(SlugLookups.class);

        // ACT
        sut.handleEvent(event("publish", "subject"));

        // ASSERT
        verify(sut.slugLookups).updateLookup(any(), eq("live"));
        verify(sut.slugLookups, never()).removeLookup(any(), any());
    }

    @Test
    public void liveLookupRemovedForDepublish() throws RepositoryException {
        // ARRANGE
        Session session = sessionWithArticle();
        SlugMaintenanceListener sut = new SlugMaintenanceListener(session);
        sut.slugLookups = mock(SlugLookups.class);

        // ACT
        sut.handleEvent(event("depublish", "subject"));

        // ASSERT
        verify(sut.slugLookups, never()).updateLookup(any(), any());
        verify(sut.slugLookups).removeLookup(any(), eq("live"));
    }

    @Test
    public void previewLookupRemovedForDelete() throws RepositoryException {
        // ARRANGE
        Session session = sessionWithArticle();
        SlugMaintenanceListener sut = new SlugMaintenanceListener(session);
        sut.slugLookups = mock(SlugLookups.class);

        // ACT
        sut.handleEvent(event("delete", "subject"));

        // ASSERT
        verify(sut.slugLookups, never()).updateLookup(any(), any());
        verify(sut.slugLookups).removeLookup(any(), eq("preview"));
    }

    @Test
    public void otherEventsIgnored() throws RepositoryException {
        // ARRANGE
        Session session = sessionWithArticle();
        SlugMaintenanceListener sut = new SlugMaintenanceListener(session);
        sut.slugLookups = mock(SlugLookups.class);

        // ACT
        sut.handleEvent(event("anotherevent", "subject"));

        // ASSERT
        verify(sut.slugLookups, never()).updateLookup(any(), any());
        verify(sut.slugLookups, never()).removeLookup(any(), any());
    }

    HippoWorkflowEvent event(String action, String subject) {
        HippoWorkflowEvent event = mock(HippoWorkflowEvent.class);
        when(event.action()).thenReturn(action);
        when(event.subjectPath()).thenReturn(subject);
        return event;
    }

    Session exceptionThrowingSession() throws RepositoryException {
        Session session = mock(Session.class);
        when(session.getNode(any())).thenThrow(new RepositoryException("arg"));
        return session;
    }

    Session sessionWithCategory() throws RepositoryException {
        Session session = mock(Session.class);
        Node node = mock(Node.class);
        Node index = mock(Node.class);
        when(node.hasNode("index")).thenReturn(true);
        when(node.getNode("index")).thenReturn(index);
        when(index.isNodeType("publishing:category")).thenReturn(true);
        when(session.getNode("subject")).thenReturn(node);
        return session;
    }

    Session sessionWithArticle() throws RepositoryException {
        Session session = mock(Session.class);
        Node handle = mock(Node.class);
        Node variant = mock(Node.class);
        when(session.getNode("subject")).thenReturn(handle);
        when(handle.hasNode("index")).thenReturn(false);
        when(handle.getName()).thenReturn("subject");
        when(handle.hasNode("subject")).thenReturn(true);
        when(handle.getNode("subject")).thenReturn(variant);
        when(variant.isNodeType("publishing:article")).thenReturn(true);
        return session;
    }
}
