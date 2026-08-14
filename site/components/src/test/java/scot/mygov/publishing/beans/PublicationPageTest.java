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

public class PublicationPageTest {

    @Test
    public void getLastUpdatedDateReturnsAncestorPublicationDate() {
        PublicationPage page = spy(new PublicationPage());
        Publication publication = mock(Publication.class);
        Calendar publicationDate = Calendar.getInstance();
        when(publication.getPublicationDate()).thenReturn(publicationDate);
        doReturn(publication).when(page).getPartOfBean();

        assertEquals(publicationDate, page.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateReturnsNullWhenNotPartOfAPublication() {
        PublicationPage page = spy(new PublicationPage());
        doReturn(mock(HippoBean.class)).when(page).getPartOfBean();

        assertNull(page.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateReturnsNullWhenNoPartOfBean() {
        PublicationPage page = spy(new PublicationPage());
        doReturn(null).when(page).getPartOfBean();

        assertNull(page.getLastUpdatedDate());
    }
}
