package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

import static scot.mygov.publishing.components.SearchBarComponent.populateAutoCompleteFlag;

public class HomeComponent extends CategoryComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        SearchBarComponent.populateSearchEnabledFlag(request);
        populateAutoCompleteFlag(request);
    }
}
