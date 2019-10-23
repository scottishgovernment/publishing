package scot.mygov.publishing.linkprocessors;

import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 23/10/2019.
 */
public class PublishingLinkTest {

    @Test
    public void getPathReturnsTrainingSlashIfMissing() {

        // ARRANGE
        HstLink wrapped = mock(HstLink.class);
        when(wrapped.getPath()).thenReturn("path");
        PublishingLink sut = new PublishingLink(wrapped);
        String expected = "path/";

        // ACT
        String actual = sut.getPath();

        // ASSERT
        assertEquals(expected, actual);
    }

    @Test
    public void getPathReturnsTrainingSlashIfAlreadyPresent() {

        // ARRANGE
        HstLink wrapped = mock(HstLink.class);
        when(wrapped.getPath()).thenReturn("path/");
        PublishingLink sut = new PublishingLink(wrapped);
        String expected = "path/";

        // ACT
        String actual = sut.getPath();

        // ASSERT
        assertEquals(expected, actual);
    }

    @Test
    public void toUrlFormAddsSlashIfMissing() {
        // ARRANGE
        HstLink wrapped = mock(HstLink.class);
        HstRequestContext context = mock(HstRequestContext.class);
        when(wrapped.toUrlForm(context, true)).thenReturn("path");
        PublishingLink sut = new PublishingLink(wrapped);
        String expected = "path/";

        // ACT
        String actual = sut.toUrlForm(context, true);

        // ASSERT
        assertEquals(expected, actual);

    }

    @Test
    public void toUrlFormAddsSlashIfAreadyPresent() {
        // ARRANGE
        HstLink wrapped = mock(HstLink.class);
        HstRequestContext context = mock(HstRequestContext.class);
        when(wrapped.toUrlForm(context, true)).thenReturn("path/");
        PublishingLink sut = new PublishingLink(wrapped);
        String expected = "path/";

        // ACT
        String actual = sut.toUrlForm(context, true);

        // ASSERT
        assertEquals(expected, actual);

    }

    @Test
    public void toUrlFormReturnsNullForNullLinkStr() {
        // ARRANGE
        HstLink wrapped = mock(HstLink.class);
        HstRequestContext context = mock(HstRequestContext.class);
        when(wrapped.toUrlForm(context, true)).thenReturn(null);
        PublishingLink sut = new PublishingLink(wrapped);

        // ACT
        String actual = sut.toUrlForm(context, true);

        // ASSERT
        assertNull(actual);

    }

}
