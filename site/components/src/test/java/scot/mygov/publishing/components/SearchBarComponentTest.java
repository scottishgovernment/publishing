package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.junit.Test;

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
        assertFormatHosdesSearch("home", true);
        assertFormatHosdesSearch("search", true);
        assertFormatHosdesSearch("searchbloomreach", true);
        assertFormatHosdesSearch("searchfunnelback", true);
        assertFormatHosdesSearch("searchresilient", true);
        assertFormatHosdesSearch("category", false);
        assertFormatHosdesSearch("article", false);
        assertFormatHosdesSearch("guide", false);
    }

    void assertFormatHosdesSearch(String format, boolean expected) {
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


}
