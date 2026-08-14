package scot.mygov.publishing.beans;

import org.junit.Test;

import java.util.Calendar;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class DsarticleTest {

    @Test
    public void getLastUpdatedDateReturnsMostRecentUpdateHistoryEntry() {
        Dsarticle dsarticle = spy(new Dsarticle());
        UpdateHistory older = mock(UpdateHistory.class);
        UpdateHistory newer = mock(UpdateHistory.class);
        Calendar olderDate = calendar(2023, 1, 1);
        Calendar newerDate = calendar(2024, 6, 15);
        when(older.getLastUpdated()).thenReturn(olderDate);
        when(newer.getLastUpdated()).thenReturn(newerDate);
        doReturn(asList(older, newer)).when(dsarticle).getUpdateHistory();

        assertEquals(newerDate, dsarticle.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateIgnoresOrderOfUpdateHistoryEntries() {
        Dsarticle dsarticle = spy(new Dsarticle());
        UpdateHistory older = mock(UpdateHistory.class);
        UpdateHistory newer = mock(UpdateHistory.class);
        Calendar olderDate = calendar(2023, 1, 1);
        Calendar newerDate = calendar(2024, 6, 15);
        when(older.getLastUpdated()).thenReturn(olderDate);
        when(newer.getLastUpdated()).thenReturn(newerDate);
        doReturn(asList(newer, older)).when(dsarticle).getUpdateHistory();

        assertEquals(newerDate, dsarticle.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateFallsBackToPublicationDateWhenNoUpdateHistory() {
        Dsarticle dsarticle = spy(new Dsarticle());
        doReturn(Collections.emptyList()).when(dsarticle).getUpdateHistory();
        Calendar publicationDate = calendar(2022, 3, 10);
        doReturn(publicationDate).when(dsarticle).getSingleProperty("hippostdpubwf:publicationDate");

        assertEquals(publicationDate, dsarticle.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateReturnsNullWhenNoHistoryAndNoPublicationDate() {
        Dsarticle dsarticle = spy(new Dsarticle());
        doReturn(Collections.emptyList()).when(dsarticle).getUpdateHistory();
        doReturn(null).when(dsarticle).getSingleProperty("hippostdpubwf:publicationDate");

        assertNull(dsarticle.getLastUpdatedDate());
    }

    static Calendar calendar(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, day);
        return calendar;
    }
}
