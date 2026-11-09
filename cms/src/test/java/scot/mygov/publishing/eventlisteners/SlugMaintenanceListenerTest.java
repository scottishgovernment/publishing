package scot.mygov.publishing.eventlisteners;

import org.junit.Test;
import org.onehippo.repository.events.HippoWorkflowEvent;
import scot.gov.publishing.sluglookup.SlugLookups;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SlugMaintenanceListenerTest {

    @Test
    public void deleteRemovesPreviewLookupEvenThoughSubjectNoLongerExists() throws RepositoryException {

        // ARRANGE - the document has already been removed by the time the event is handled, so neither
        // nodeExists nor getNodeByIdentifier can find it.
        Session session = mock(Session.class);
        when(session.nodeExists(any())).thenReturn(false);
        when(session.getNodeByIdentifier(any())).thenThrow(new ItemNotFoundException());

        SlugMaintenanceListener sut = new SlugMaintenanceListener(session);
        sut.slugLookups = mock(SlugLookups.class);

        HippoWorkflowEvent event = mock(HippoWorkflowEvent.class);
        when(event.action()).thenReturn("delete");
        when(event.subjectPath()).thenReturn("/content/documents/mygov/articles/who-we-are");
        when(event.subjectId()).thenReturn("some-id");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(sut.slugLookups).removeLookup("/articles/who-we-are", "mygov", "global", "preview");
    }

    @Test
    public void scheduledDepublishDoesNotRemoveLiveLookup() throws RepositoryException {

        // ARRANGE - when a depublication is scheduled the "depublish" event fires straight away,
        // but the document is still live until the scheduled job runs.
        Session session = mock(Session.class);
        SlugMaintenanceListener sut = listenerWithDocument(session, "live", "preview");
        HippoWorkflowEvent event = depublishEvent();

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(sut.slugLookups, never()).removeLookup(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void depublishRemovesLiveLookupOnceDocumentIsOffline() throws RepositoryException {

        // ARRANGE
        Session session = mock(Session.class);
        SlugMaintenanceListener sut = listenerWithDocument(session, "preview");
        HippoWorkflowEvent event = depublishEvent();

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(sut.slugLookups).removeLookup("/articles/who-we-are", "mygov", "global", "live");
    }

    HippoWorkflowEvent depublishEvent() {
        HippoWorkflowEvent event = mock(HippoWorkflowEvent.class);
        when(event.action()).thenReturn("depublish");
        when(event.subjectPath()).thenReturn("/content/documents/mygov/articles/who-we-are");
        return event;
    }

    SlugMaintenanceListener listenerWithDocument(Session session, String... availability) throws RepositoryException {
        Node handle = mock(Node.class);
        Node variant = mock(Node.class);
        Node site = mock(Node.class);
        when(session.nodeExists("/content/documents/mygov/articles/who-we-are")).thenReturn(true);
        when(session.getNode("/content/documents/mygov/articles/who-we-are")).thenReturn(handle);
        when(handle.getName()).thenReturn("who-we-are");
        when(handle.getPath()).thenReturn("/content/documents/mygov/articles/who-we-are");
        when(handle.hasNode("who-we-are")).thenReturn(true);
        when(handle.getNode("who-we-are")).thenReturn(variant);
        when(handle.getAncestor(3)).thenReturn(site);
        when(site.getName()).thenReturn("mygov");
        when(variant.hasProperty("publishing:slug")).thenReturn(true);

        NodeIterator variants = mock(NodeIterator.class);
        when(variants.hasNext()).thenReturn(true, false);
        when(variants.nextNode()).thenReturn(variant);
        when(handle.getNodes("who-we-are")).thenReturn(variants);

        Value[] values = new Value[availability.length];
        for (int i = 0; i < availability.length; i++) {
            values[i] = mock(Value.class);
            when(values[i].getString()).thenReturn(availability[i]);
        }
        Property availabilityProperty = mock(Property.class);
        when(availabilityProperty.getValues()).thenReturn(values);
        when(variant.hasProperty("hippo:availability")).thenReturn(true);
        when(variant.getProperty("hippo:availability")).thenReturn(availabilityProperty);

        SlugMaintenanceListener sut = new SlugMaintenanceListener(session);
        sut.slugLookups = mock(SlugLookups.class);
        return sut;
    }
}
