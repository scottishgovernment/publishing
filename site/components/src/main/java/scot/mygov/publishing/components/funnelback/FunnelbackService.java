package scot.mygov.publishing.components.funnelback;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.apache.commons.lang3.time.StopWatch;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.site.HstServices;
import org.onehippo.cms7.crisp.api.broker.ResourceServiceBroker;
import org.onehippo.cms7.crisp.api.resource.Resource;
import org.onehippo.cms7.crisp.api.resource.ResourceBeanMapper;
import org.onehippo.cms7.crisp.hst.module.CrispHstServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import scot.mygov.publishing.components.funnelback.model.FunnelbackSearchResponse;
import scot.mygov.publishing.components.funnelback.model.Suggestion;
import scot.mygov.publishing.components.funnelback.postprocessing.CompositePostProcessor;
import scot.mygov.publishing.components.funnelback.postprocessing.PaginationProcessor;
import scot.mygov.publishing.components.funnelback.postprocessing.PostProcessor;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class FunnelbackService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchComponent.class);

    private static final String FUNNELBACK = "funnelback";

    // see https://community.funnelback.com/knowledge-base/implementation/search-interface/auto-completion/auto-completion-web-service-suggest.json
    // fmt=json++ specifies that full result is returned, sort = 0 indicates to sort by score.
    private static final String SUGGEST_URL = "/suggest.json?partial_query={partial_query}&collection=govscot~sp-mygov&fmt=json++&show=6&sort=0";

    FallbackService fallbackService = new FallbackService();

    public List<String> getSuggestions(String partialQuery) {
        Map<String, Object> params = Collections.singletonMap("partial_query", partialQuery);
        ResourceServiceBroker broker = CrispHstServices.getDefaultResourceServiceBroker(HstServices.getComponentManager());
        Resource results = broker.findResources(FUNNELBACK, SUGGEST_URL, params);
        ResourceBeanMapper resourceBeanMapper = broker.getResourceBeanMapper(FUNNELBACK);
        Collection<Suggestion> suggestions = resourceBeanMapper.mapCollection(results.getChildren(), Suggestion.class);
        return suggestions.stream().map(Suggestion::getDisp).collect(toList());
    }

    @HystrixCommand(
            fallbackMethod = "getSearchResponseFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
            }
    )
    public FunnelbackSearchResponse getSearchResponse(HstRequest request, String query, String page) {
        request.setAttribute("implementation", FUNNELBACK);
        int rank = ((Integer.parseInt(page) - 1) * 10) + 1;
        Map<String, Object> params = paramMap(query, rank);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ResourceServiceBroker broker = CrispHstServices.getDefaultResourceServiceBroker(HstServices.getComponentManager());
        Resource results = broker.resolve(FUNNELBACK, "/search.json?query={query}&start_rank={rank}&collection=govscot~sp-mygov&cat=sitesearch&profile=_default", params);
        ResourceBeanMapper resourceBeanMapper = broker.getResourceBeanMapper(FUNNELBACK);
        stopWatch.stop();
        FunnelbackSearchResponse response = resourceBeanMapper.map(results, FunnelbackSearchResponse.class);
        PostProcessor postProcessor = CompositePostProcessor.processor(new PaginationProcessor());
        postProcessor.process(response);
        LOG.info("funnelback search \"{}\" took {}, {} results", query, stopWatch.getTime(), response.getResponse().getResultPacket().getResultsSummary().getTotalMatching());
        return response;
    }

    Map<String, Object> paramMap(String query, int rank) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        params.put("rank", rank);
        return params;
    }

    public FunnelbackSearchResponse getSearchResponseFallback(HstRequest request, String query, String page) {
        return fallbackService.getSearchResponse(request, query, page);
    }

}
