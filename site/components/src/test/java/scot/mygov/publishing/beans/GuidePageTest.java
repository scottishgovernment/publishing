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

public class GuidePageTest {

    @Test
    public void getLastUpdatedDateReturnsAncestorGuideLastUpdatedDate() {
        GuidePage page = spy(new GuidePage());
        Guide guide = mock(Guide.class);
        Calendar lastUpdatedDate = Calendar.getInstance();
        when(guide.getLastUpdatedDate()).thenReturn(lastUpdatedDate);
        doReturn(guide).when(page).getPartOfBean();

        assertEquals(lastUpdatedDate, page.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateReturnsNullWhenNotPartOfAGuide() {
        GuidePage page = spy(new GuidePage());
        doReturn(mock(HippoBean.class)).when(page).getPartOfBean();

        assertNull(page.getLastUpdatedDate());
    }

    @Test
    public void getLastUpdatedDateReturnsNullWhenNoPartOfBean() {
        GuidePage page = spy(new GuidePage());
        doReturn(null).when(page).getPartOfBean();

        assertNull(page.getLastUpdatedDate());
    }
}
