package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import scot.gov.publishing.hippo.funnelback.component.SearchBuilder;

import java.util.*;

import static scot.mygov.publishing.components.FilteredResultsComponent.*;

@ParametersInfo(type = FilteredResultsSideComponentInfo.class)
public class FilteredResultsSideComponent extends EssentialsContentComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        FilteredResultsSideComponentInfo info = getComponentParametersInfo(request);
        SearchBuilder searchBuilder = new SearchBuilder()
                .query(param(request, "q"))
                .fromDate(date(request, "begin"))
                .toDate(date(request, "end"));
        Map<String, String> topicsMap = topicsMap(request.getRequestContext());
        FilteredResultsComponent.getTopics(request).stream().forEach(topic -> searchBuilder.topics(topic, topicsMap));
        Map<String, String> publicationTypesMap = publicationTypesMap(request.getRequestContext());
        FilteredResultsComponent.getPublicationTypes(request).stream().forEach(type -> searchBuilder.publicationTypes(type, publicationTypesMap));
        request.setAttribute("publicationTypesMap", publicationTypesMap);
        request.setAttribute("topicsMap", topicsMap);
        request.setAttribute("search", searchBuilder.build());
        request.setAttribute("includePublicationTypesFilter", info.getIncludePublicationTypesFilter());
    }
}
