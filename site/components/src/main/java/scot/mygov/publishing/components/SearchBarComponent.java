package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import scot.gov.publishing.hippo.funnelback.component.ResilientSearchComponent;
import scot.gov.publishing.hippo.funnelback.component.SearchSettings;

import static org.apache.commons.lang3.StringUtils.equalsAny;

/**
 * Component for the search bar.
 */
public class SearchBarComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        setSearchVisibility(request);
        populateAutoCompleteFlag(request);
        populateSearchEnabledFlag(request);
    }

    void setSearchVisibility(HstRequest request) {
        HstComponentConfiguration componentConfig = request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getHstComponentConfiguration();
        String formatName = componentConfig.getName();
        request.setAttribute("hideSearch", equalsAny(formatName, "home") || formatName.startsWith("search"));
    }

    /**
     * determine if auto complete should be used for search bars
     */
    static void populateAutoCompleteFlag(HstRequest request) {
        SearchSettings searchSettings = ResilientSearchComponent.searchSettings();
        request.setAttribute("autoCompleteEnabled", autoCompleteEnabled(searchSettings));
    }

    static boolean autoCompleteEnabled(SearchSettings searchSettings) {
        return searchEnabled(searchSettings) &&
                !"bloomreach".equals(searchSettings.getSearchType());
    }

    /**
     * determine if the search is enabled
     */
    static void populateSearchEnabledFlag(HstRequest request) {
        SearchSettings searchSettings = ResilientSearchComponent.searchSettings();
        request.setAttribute("searchEnabled", searchEnabled(searchSettings));
    }

    static boolean searchEnabled(SearchSettings searchSettings) {
        boolean searchEnabled = true;
        if (!searchSettings.isEnabled()) {
            searchEnabled = false;
        }
        return searchEnabled;
    }
}
