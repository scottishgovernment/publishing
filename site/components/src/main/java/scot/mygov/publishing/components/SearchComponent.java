package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;

import static scot.mygov.publishing.components.SiteHeaderComponent.populateAutoCompleteFlag;

public class SearchComponent extends EssentialsContentComponent {

    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        populateAutoCompleteFlag(request);
    }

}
