package scot.mygov.publishing.linkprocessors;

import org.hippoecm.hst.core.linking.HstLink;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TrailingSlashLinkProcessorTest {

    @Test
    public void postProcessAddsTrailingSlashIfMissing() {
        // ARRANGE
        TrailingSlashLinkProcessor sut = new TrailingSlashLinkProcessor();
        HstLink input = link("sectors/energy");

        // ACT
        String actual = sut.doPostProcess(input).getPath();

        // ASSERT
        String expected = input.getPath() + "/";
        assertEquals(expected, actual);
    }

    @Test
    public void postProcessIgnoresPathsWithTrailingSlashes() {
        // ARRANGE
        TrailingSlashLinkProcessor sut = new TrailingSlashLinkProcessor();
        HstLink input = link("country-profiles/germany/");

        // ACT
        String actual = sut.doPostProcess(input).getPath();

        // ASSERT
        assertEquals(input.getPath(), actual);
    }

    @Test
    public void postProcessIgnoresExtensionPaths() {
        //ARRANGE
        TrailingSlashLinkProcessor sut = new TrailingSlashLinkProcessor();
        HstLink input = link("assets/images/hero/CountryNavImage-x2.png");

        // ACT
        String actual = sut.doPostProcess(input).getPath();

        // ASSERT
        assertEquals(input.getPath(), actual);
    }

    HstLink link(String path) {
        HstLink link = mock(HstLink.class);
        when(link.getPath()).thenReturn(path);
        return link;
    }
}
