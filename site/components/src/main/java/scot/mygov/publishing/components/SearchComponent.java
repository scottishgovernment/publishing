package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import scot.gov.publishing.hippo.funnelback.component.ResilientSearchComponent;
import scot.gov.publishing.hippo.funnelback.component.SearchSettings;

public class SearchComponent extends EssentialsContentComponent {

    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        SearchSettings searchsettings = ResilientSearchComponent.searchSettings();
        request.setAttribute("autoCompleteEnabled", autoCompleteEnabled(searchsettings));
    }

    boolean autoCompleteEnabled(SearchSettings searchSettings) {
        return searchSettings.isEnabled() && !"bloomreach".equals(searchSettings.getSearchType());
    }

}
