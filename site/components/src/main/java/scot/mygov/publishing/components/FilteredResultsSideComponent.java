package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

import java.util.Map;

import static scot.mygov.publishing.components.FilteredResultsComponent.topics;

public class FilteredResultsSideComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        Map<String, String> topics = topics(request);
        request.setAttribute("topics", topics);
    }

}
