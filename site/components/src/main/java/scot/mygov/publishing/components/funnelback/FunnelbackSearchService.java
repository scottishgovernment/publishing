package scot.mygov.publishing.components.funnelback;

import org.hippoecm.hst.configuration.hosting.VirtualHost;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.site.HstServices;
import org.onehippo.cms7.crisp.api.broker.ResourceServiceBroker;
import org.onehippo.cms7.crisp.api.resource.Resource;
import org.onehippo.cms7.crisp.api.resource.ResourceBeanMapper;
import org.onehippo.cms7.crisp.api.resource.ResourceException;
import org.onehippo.cms7.crisp.hst.module.CrispHstServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scot.gov.publishing.hippo.funnelback.model.*;

import scot.mygov.publishing.beans.Searchsettings;
import scot.mygov.publishing.components.funnelback.postprocess.CuratorPostProcessor;
import scot.mygov.publishing.components.funnelback.postprocess.PaginationBuilder;
import scot.mygov.publishing.components.funnelback.postprocess.PostProcessor;
import scot.mygov.publishing.components.funnelback.postprocess.ResultLinkRewriter;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.equalsAny;

@Service
@Component("scot.mygov.publishing.components.funnelback.FunnelbackService")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FunnelbackSearchService implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(FunnelbackSearchService.class);

    private static final String URL_TEMPLATE
            = "/search.json?query={query}&start_rank={rank}&collection={collection}";

    private static final String SUGGEST_URL
            = "/suggest.json?partial_query={partial_query}&show={show}&sort={sort}&fmt={fmt}&collection={collection}";

    private static final String FUNNELBACK_RESOURCE_SPACE = "funnelback";

    private static CuratorPostProcessor CURATOR_POST_PROCESSOR = new CuratorPostProcessor();

    private String collection;

    private List<String> sites;

    @Override
    public SearchResponse performSearch(Search search, Searchsettings searchsettings) {
        int rank = getRank(search.getPage());
        Map<String, Object> params = searchParamMap(search.getQuery(), rank);
        ResourceServiceBroker broker = CrispHstServices.getDefaultResourceServiceBroker(HstServices.getComponentManager());
        String urlTemplate = getUrlTemplate(search.isEnableSuplimentaryQueries());
        Resource results = broker.resolve(FUNNELBACK_RESOURCE_SPACE, urlTemplate, params);
        ResourceBeanMapper resourceBeanMapper = broker.getResourceBeanMapper(FUNNELBACK_RESOURCE_SPACE);
        FunnelbackSearchResponse response = resourceBeanMapper.map(results, FunnelbackSearchResponse.class);
        postProcessSearchresponse(search, response);
        Pagination pagination = createPagination(search, response);
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setType(SearchResponse.Type.FUNNELBACK);
        searchResponse.setQuestion(response.getQuestion());
        searchResponse.setResponse(response.getResponse());
        searchResponse.setPagination(pagination);
        return searchResponse;
    }

    int getRank(int page) {
        return ((page - 1) * 10) + 1;
    }

    public List<String> getSuggestions(String partialQuery) {
        try {
            return doGetSuggestions(partialQuery);
        } catch (ResourceException e) {
            LOG.error("getSuggestions failed", e);
            return Collections.emptyList();
        }
    }

    List<String> doGetSuggestions(String partialQuery) {
        Map<String, Object> params = suggestionsParamMap(partialQuery);
        ResourceServiceBroker broker = CrispHstServices.getDefaultResourceServiceBroker(HstServices.getComponentManager());
        Resource results = broker.findResources(FUNNELBACK_RESOURCE_SPACE, SUGGEST_URL, params);
        ResourceBeanMapper resourceBeanMapper = broker.getResourceBeanMapper(FUNNELBACK_RESOURCE_SPACE);
        Collection<Suggestion> suggestions = resourceBeanMapper.mapCollection(results.getChildren(), Suggestion.class);
        return suggestions.stream().map(Suggestion::getDisp).collect(toList());
    }

    String getUrlTemplate(boolean qsupOff) {
        // add the qusup param if it is switched off.  Otherwise the param is omitted so that it defaults.
        return qsupOff ? URL_TEMPLATE + "&qsup=off" : URL_TEMPLATE;
    }

    Pagination createPagination(Search search, FunnelbackSearchResponse response) {
        ResultsSummary summary = response.getResponse().getResultPacket().getResultsSummary();
        return new PaginationBuilder(search.getRequestUrl()).getPagination(summary, search.getQuery());
    }

    void postProcessSearchresponse(Search search, FunnelbackSearchResponse response) {
        rewriteLinks(response, search.getRequest());
        removeDuplucateQSups(response);
        extractSimpleHtmlMessagesFromCurator(response);
    }

    void extractSimpleHtmlMessagesFromCurator(FunnelbackSearchResponse response) {
        CURATOR_POST_PROCESSOR.process(response);
    }

    void removeDuplucateQSups(FunnelbackSearchResponse response) {
        ResultPacket resultPacket = response.getResponse().getResultPacket();
        String query = resultPacket.getQuery();
        List<QSup> filteredQsups = resultPacket.getQsups()
                .stream()
                .filter(qsup -> !qsup.getQuery().equals(query))
                .collect(toList());
        resultPacket.setQsups(filteredQsups);
    }

    void rewriteLinks(FunnelbackSearchResponse response, HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        VirtualHost virtualHost = context.getResolvedMount().getMount().getVirtualHost();
        String hostGroupName = virtualHost.getHostGroupName();
        if (useRewriter(hostGroupName)) {
            PostProcessor postProcessor = new ResultLinkRewriter(virtualHost.getName(), sites);
            postProcessor.process(response);
        }
    }

    boolean useRewriter(String hostGroupName) {
        return !equalsAny(hostGroupName, "production", "dev-localhost");
    }

    Map<String, Object> searchParamMap(String query, int rank) {

        Map<String, Object> params = new HashMap<>();
        params.put("query", defaultIfBlank(query, ""));
        params.put("rank", rank);
        params.put("collection", collection);
        return params;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public List<String> getSites() {
        return sites;
    }

    public void setSites(List<String> sites) {
        this.sites = sites;
    }

    Map<String, Object> suggestionsParamMap(String partialQuery) {
        Map<String, Object> params = new HashMap<>();
        params.put("partial_query", defaultIfBlank(partialQuery, ""));
        params.put("collection", collection);
        params.put("show", 6);
        params.put("sort", 0);
        params.put("fmt", "json++");
        return params;
    }

}