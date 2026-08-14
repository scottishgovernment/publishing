package scot.mygov.publishing.beans;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class BaseDocumentTest {

    @Test
    public void getLastUpdatedDateReturnsPublicationDate() {
        BaseDocument document = spy(new BaseDocument());
        Calendar publicationDate = Calendar.getInstance();
        doReturn(publicationDate).when(document).getSingleProperty("publishing:publicationDate");

        assertEquals(publicationDate, document.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateReturnsNullWhenPublicationDateNotSet() {
        BaseDocument document = spy(new BaseDocument());
        doReturn(null).when(document).getSingleProperty("publishing:publicationDate");

        assertNull(document.getLastUpdatedDate());
    }
}
