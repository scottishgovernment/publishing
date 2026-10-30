package scot.mygov.publishing.eventlisteners;

import org.junit.Test;
import org.onehippo.repository.events.HippoWorkflowEvent;
import scot.gov.publishing.sluglookup.SlugLookups;

import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
}
