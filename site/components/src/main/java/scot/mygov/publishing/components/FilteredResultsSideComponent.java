package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import scot.gov.publishing.hippo.funnelback.component.ResilientSearchComponent;
import scot.gov.publishing.hippo.funnelback.component.SearchBuilder;
import scot.gov.publishing.hippo.funnelback.component.SearchSettings;

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
        request.setAttribute("search", searchBuilder.build());

        Map<String, String> topicsMap = new TopicsProvider().get(request.getRequestContext());
        FilteredResultsComponent.getTopics(request).stream().forEach(topic -> searchBuilder.topics(topic, topicsMap));
        request.setAttribute("topicsMap", topicsMap);

        Map<String, String> publicationTypesMap = new PublicationTypesProvider(info.getIncludeNews()).get(request.getRequestContext());
        FilteredResultsComponent.getPublicationTypes(request).stream().forEach(type -> searchBuilder.publicationTypes(type, publicationTypesMap));
        request.setAttribute("publicationTypesMap", publicationTypesMap);
        request.setAttribute("includePublicationTypesFilter", info.getIncludePublicationTypesFilter());

        Map<String, String> languagesMap = languagesMap(request.getRequestContext());
        request.setAttribute("languagesMap", languagesMap);
        request.setAttribute("includeLanguages", info.getIncludeLanguages());

        Map<String, String> accessibilityFeaturesMap = accessibilityFeaturesMap(request.getRequestContext());
        request.setAttribute("accessibilityFeaturesMap", accessibilityFeaturesMap);
        request.setAttribute("includeAccessibilityFeatures", info.getIncludeAccessibilityFeatures());

        SearchSettings searchSettings = ResilientSearchComponent.searchSettings();
        request.setAttribute("showFilters", searchSettings.isShowFilters());
    }
}
