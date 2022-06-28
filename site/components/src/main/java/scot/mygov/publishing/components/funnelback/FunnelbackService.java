package scot.mygov.publishing.components.funnelback;

import org.hippoecm.hst.configuration.hosting.VirtualHost;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.site.HstServices;
import org.hippoecm.hst.util.HstRequestUtils;
import org.onehippo.cms7.crisp.api.broker.ResourceServiceBroker;
import org.onehippo.cms7.crisp.api.resource.Resource;
import org.onehippo.cms7.crisp.api.resource.ResourceBeanMapper;
import org.onehippo.cms7.crisp.hst.module.CrispHstServices;
import org.springframework.stereotype.Service;
import scot.gov.publishing.hippo.funnelback.model.FunnelbackSearchResponse;
import scot.gov.publishing.hippo.funnelback.model.Pagination;
import scot.gov.publishing.hippo.funnelback.model.ResultsSummary;
import scot.mygov.publishing.components.funnelback.postprocess.PaginationBuilder;
import scot.mygov.publishing.components.funnelback.postprocess.PostProcessor;
import scot.mygov.publishing.components.funnelback.postprocess.ResultLinkRewriter;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.equalsAny;

@Service
public class FunnelbackService {

    private static final String URL_TEMPLATE
            = "/search.json?query={query}&start_rank={rank}&collection={collection}";

    private String collection;

    private List<String> sites;

    void performSearch(String query, boolean qsupOff, int rank, HstRequest request) {

        Map<String, Object> params = paramMap(query, rank);
        ResourceServiceBroker broker = CrispHstServices.getDefaultResourceServiceBroker(HstServices.getComponentManager());
        String urlTemplate = getUrlTemplate(qsupOff);
        Resource results = broker.resolve("funnelback", urlTemplate, params);
        ResourceBeanMapper resourceBeanMapper = broker.getResourceBeanMapper("funnelback");
        FunnelbackSearchResponse response = resourceBeanMapper.map(results, FunnelbackSearchResponse.class);

        rewriteLinks(request, response);
        request.setAttribute("response", response.getResponse());
        request.setAttribute("question", response.getQuestion());
        populatePagination(request, response.getResponse().getResultPacket().getResultsSummary(), query);
    }

    String getUrlTemplate(boolean qsupOff) {
        // add the qusup param if it is switched off.  Otherwise the param is omitted so that it defaults.
        return qsupOff ? URL_TEMPLATE + "&qsup=off" : URL_TEMPLATE;
    }

    void populatePagination(HstRequest request, ResultsSummary summary, String query) {
        HttpServletRequest servletRequest = request.getRequestContext().getServletRequest();
        String url = HstRequestUtils.getExternalRequestUrl(servletRequest, false);
        Pagination pagination = new PaginationBuilder(url).getPagination(summary, query);
        request.setAttribute("pagination", pagination);
    }

    void rewriteLinks(HstRequest request, FunnelbackSearchResponse response) {
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

    Map<String, Object> paramMap(String query, int rank) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
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

}