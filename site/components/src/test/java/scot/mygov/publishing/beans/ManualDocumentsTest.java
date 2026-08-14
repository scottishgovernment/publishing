package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ManualDocumentsTest {

    @Test
    public void getLastUpdatedDateReturnsAncestorPublicationDate() {
        ManualDocuments document = spy(new ManualDocuments());
        Publication publication = mock(Publication.class);
        Calendar publicationDate = Calendar.getInstance();
        when(publication.getPublicationDate()).thenReturn(publicationDate);
        doReturn(publication).when(document).getPartOfBean();

        assertEquals(publicationDate, document.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateReturnsNullWhenNotPartOfAPublication() {
        ManualDocuments document = spy(new ManualDocuments());
        doReturn(mock(HippoBean.class)).when(document).getPartOfBean();

        assertNull(document.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateReturnsNullWhenNoPartOfBean() {
        ManualDocuments document = spy(new ManualDocuments());
        doReturn(null).when(document).getPartOfBean();

        assertNull(document.getLastUpdatedDate());
    }
}
