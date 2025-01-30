package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import scot.gov.publishing.hippo.funnelback.component.SearchBuilder;

import java.util.*;

import static scot.mygov.publishing.components.FilteredResultsComponent.*;

public class FilteredResultsSideComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        Map<String, String> topicsMap = topicsMap(request);
        SearchBuilder searchBuilder = new SearchBuilder()
                .query(param(request, "q"))
                .fromDate(date(request, "begin"))
                .toDate(date(request, "end"));
        FilteredResultsComponent.getTopics(request).stream().forEach(topic -> searchBuilder.topics(topic, topicsMap));
        request.setAttribute("topicsMap", topicsMap);
        request.setAttribute("search", searchBuilder.build());
    }
}
