package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.junit.Test;
import org.mockito.Mockito;
import scot.gov.publishing.hippo.funnelback.component.ResilientSearchComponent;
import scot.gov.publishing.hippo.funnelback.component.SearchSettings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 11/11/2022.
 */
public class SearchBarComponentTest {

    @Test
    public void displaysSearchBarInHeaderForRightFormats() {
        assertFormatHidesSearch("home", true);
        assertFormatHidesSearch("search", true);
        assertFormatHidesSearch("searchbloomreach", true);
        assertFormatHidesSearch("searchfunnelback", true);
        assertFormatHidesSearch("searchresilient", true);
        assertFormatHidesSearch("category", false);
        assertFormatHidesSearch("article", false);
        assertFormatHidesSearch("guide", false);
    }

    void assertFormatHidesSearch(String format, boolean expected) {
        SearchBarComponent sut = new SearchBarComponent();
        ResolvedSiteMapItem resolvedSiteMapItem = mock(ResolvedSiteMapItem.class);
        HstComponentConfiguration hstComponentConfiguration = mock(HstComponentConfiguration.class);
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getResolvedSiteMapItem()).thenReturn(resolvedSiteMapItem);
        when(resolvedSiteMapItem.getHstComponentConfiguration()).thenReturn(hstComponentConfiguration);
        when(hstComponentConfiguration.getName()).thenReturn(format);
        sut.setSearchVisibility(request);
        verify(request).setAttribute(eq("hideSearch"), eq(expected));
    }

    @Test
    public void autoCompleteDisabledIfSearchDisabled() {
        // ARRANGE
        SearchSettings searchSettings = new SearchSettings();
        searchSettings.setEnabled(false);

        // ACT
        boolean autoCompleteEnabled = SearchBarComponent.autoCompleteEnabled(searchSettings);

        // ASSERT
        assertFalse(autoCompleteEnabled);
    }

    @Test
    public void autoCompleteDisabledIfSearchTypeIsBloomreach() {
        // ARRANGE
        SearchSettings searchSettings = new SearchSettings();
        searchSettings.setEnabled(true);
        searchSettings.setSearchType("bloomreach");

        // ACT
        boolean autoCompleteEnabled = SearchBarComponent.autoCompleteEnabled(searchSettings);

        // ASSERT
        assertFalse(autoCompleteEnabled);
    }

    @Test
    public void autoCompleteEnabledIfSearchTypeIsFunnelback() {
        // ARRANGE
        SearchSettings searchSettings = new SearchSettings();
        searchSettings.setEnabled(true);
        searchSettings.setSearchType("funnelback");

        // ACT
        boolean autoCompleteEnabled = SearchBarComponent.autoCompleteEnabled(searchSettings);

        // ASSERT
        assertTrue(autoCompleteEnabled);
    }

    @Test
    public void autoCompleteEnabledIfSearchTypeIsResilient() {
        // ARRANGE
        SearchSettings searchSettings = new SearchSettings();
        searchSettings.setEnabled(true);
        searchSettings.setSearchType("resilient");

        // ACT
        boolean autoCompleteEnabled = SearchBarComponent.autoCompleteEnabled(searchSettings);

        /// ASSERT
        assertTrue(autoCompleteEnabled);
    }

}
