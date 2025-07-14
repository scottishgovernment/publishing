package scot.mygov.publishing.eventlisteners;

import org.junit.Test;
import org.onehippo.repository.events.HippoWorkflowEvent;
import scot.gov.imageprocessing.exif.Exif;
import scot.gov.imageprocessing.thumbnails.FileType;
import scot.mygov.publishing.test.TestUtil;

import javax.jcr.*;
import java.util.Collections;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static scot.mygov.publishing.eventlisteners.ThumbnailsEventListener.HASH_PROPERTY;

public class ThumbnailsEventListenerTest {

    @Test
    public void handleEventDoesNotSubmitJobForNonUploadEvent() {

        // ARRANGE
        ThumbnailsEventListener sut = new ThumbnailsEventListener(anySession());
        sut.executor = mock(ExecutorService.class);

        // ACT
        sut.handleEvent(successfulNonUploadEvent());

        // ASSERT
        verify(sut.executor,never()).submit(any(Runnable.class));
    }

    @Test
    public void handleEventSubmitsJobForUploadEvent() {

        // ARRANGE
        ThumbnailsEventListener sut = new ThumbnailsEventListener(anySession());
        sut.executor = mock(ExecutorService.class);

        // ACT
        sut.handleEvent(successfulUploadEvent());

        // ASSERT
        verify(sut.executor).submit(any(Runnable.class));
    }

    @Test
    public void canHandleEventReturnsFalseIfEventUnsuccessful() {
        ThumbnailsEventListener sut = new ThumbnailsEventListener(anySession());
        assertFalse(sut.canHandleEvent(unsuccessfulEvent()));
    }

    @Test
    public void canHandleEventReturnsFalseIfNotUpload() {
        ThumbnailsEventListener sut = new ThumbnailsEventListener(anySession());
        assertFalse(sut.canHandleEvent(successfulNonUploadEvent()));
    }

    @Test
    public void canHandleEventReturnsTrueForSuccessfulUploadEvent() {
        ThumbnailsEventListener sut = new ThumbnailsEventListener(anySession());
        assertTrue(sut.canHandleEvent(successfulUploadEvent()));
    }

    @Test
    public void doHandleEventDoesNotRethrowRepoException() throws RepositoryException {
        ThumbnailsEventListener sut = new ThumbnailsEventListener(exceptionThrowingSession());
        assertTrue(sut.canHandleEvent(successfulUploadEvent()));
        sut.doHandleEventWithLogging(successfulUploadEvent());

        // ASSERT - nothing to assert ... no exceptiion should have been thrown.
    }

    @Test
    public void ensureThumbnailDoesNotUpdateThumbnailsIfHashIsSame() throws Exception {

        ThumbnailsEventListener sut = new ThumbnailsEventListener(anySession());
        String hash = "hash";
        sut.hashProvider = node -> hash;

        Node resource = mock(Node.class);
        Node thumbnail = mock(Node.class);
        Node document = documentWithHashPropertyAndThumbnail(hash, thumbnail);
        when(resource.getParent()).thenReturn(document);
        Property hashProp = property(hash);
        when(document.hasProperty(HASH_PROPERTY)).thenReturn(true);
        when(document.getProperty(HASH_PROPERTY)).thenReturn(hashProp);

        // ACT
        sut.ensureThumbnail(resource);

        // this is what is called when we wnt to delete and then update the thumbanails
        verify(thumbnail, never()).remove();
    }

    @Test
    public void setPageCountCallsExifForPdfs() throws RepositoryException {
        // ARRANGE
        ThumbnailsEventListener sut = new ThumbnailsEventListener(anySession());
        sut.exif = mock(Exif.class);
        when(sut.exif.pageCount(any())).thenReturn(100L);
        Node node = mock(Node.class);
        Binary data = mock(Binary.class);

        // ACT
        sut.setPageCount(node, data, FileType.PDF.getMimeType());

        // ASSERT
        verify(node).setProperty("publishing:pageCount", 100L);
    }

    @Test
    public void setPageCountZeroForNonPdfs() throws RepositoryException {
        // ARRANGE
        ThumbnailsEventListener sut = new ThumbnailsEventListener(anySession());
        sut.exif = mock(Exif.class);
        when(sut.exif.pageCount(any())).thenReturn(100L);
        Node node = mock(Node.class);
        Binary data = mock(Binary.class);

        // ACT
        sut.setPageCount(node, data, FileType.DOCX.getMimeType());

        // ASSERT
        verify(node).setProperty("publishing:pageCount", 0);
    }

    Node documentWithHashPropertyAndThumbnail(String hash, Node thumbnail) throws RepositoryException {
        Node document = mock(Node.class);
        Property hashProp = property(hash);
        when(document.hasProperty(HASH_PROPERTY)).thenReturn(true);
        when(document.getProperty(HASH_PROPERTY)).thenReturn(hashProp);
        when(document.getNodes("publishing:thumbnails"))
                .thenReturn(TestUtil.iterator(Collections.singletonList(thumbnail)));
        return document;
    }

    Property property(String value) throws RepositoryException {
        Property p = mock(Property.class);
        when(p.getString()).thenReturn(value);
        return p;
    }

    HippoWorkflowEvent unsuccessfulEvent() {
        return event(false, "anyaction");
    }

    HippoWorkflowEvent successfulNonUploadEvent() {
        return event(true, "anyaction");
    }

    HippoWorkflowEvent successfulUploadEvent() {
        return event(true, "upload");
    }

    HippoWorkflowEvent event(boolean success, String action) {
        HippoWorkflowEvent event = mock(HippoWorkflowEvent.class);
        when(event.success()).thenReturn(success);
        when(event.action()).thenReturn(action);
        return event;
    }

    Session anySession() {
        return mock(Session.class);
    }

    Session exceptionThrowingSession() throws RepositoryException {
        Session session = mock(Session.class);
        when(session.getNodeByIdentifier(any())).thenThrow(new RepositoryException("argh"));
        return session;
    }

}
