package scot.mygov.publishing.beans;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class BaseTest {

    @Test
    public void getLastUpdatedDateReturnsOwnFieldWhenPresent() {
        Base base = spy(new Base());
        Calendar lastUpdatedDate = Calendar.getInstance();
        doReturn(lastUpdatedDate).when(base).getSingleProperty("publishing:lastUpdatedDate");

        assertEquals(lastUpdatedDate, base.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateFallsBackToPublicationDateWhenOwnFieldEmpty() {
        Base base = spy(new Base());
        doReturn(null).when(base).getSingleProperty("publishing:lastUpdatedDate");
        Calendar publicationDate = Calendar.getInstance();
        doReturn(publicationDate).when(base).getSingleProperty("publishing:publicationDate");

        assertEquals(publicationDate, base.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateReturnsNullWhenNeitherFieldIsSet() {
        Base base = spy(new Base());
        doReturn(null).when(base).getSingleProperty("publishing:lastUpdatedDate");
        doReturn(null).when(base).getSingleProperty("publishing:publicationDate");

        assertNull(base.getLastUpdatedDate());
    }
}
