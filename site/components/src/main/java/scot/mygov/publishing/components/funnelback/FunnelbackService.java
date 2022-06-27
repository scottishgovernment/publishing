package scot.mygov.publishing.components.funnelback;

import org.hippoecm.hst.configuration.hosting.VirtualHost;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.site.HstServices;
import org.onehippo.cms7.crisp.api.broker.ResourceServiceBroker;
import org.onehippo.cms7.crisp.api.resource.Resource;
import org.onehippo.cms7.crisp.api.resource.ResourceBeanMapper;
import org.onehippo.cms7.crisp.hst.module.CrispHstServices;
import org.springframework.stereotype.Service;
import scot.gov.publishing.hippo.funnelback.model.FunnelbackSearchResponse;
import scot.mygov.publishing.components.funnelback.postprocess.PaginationProcessor;
import scot.mygov.publishing.components.funnelback.postprocess.PostProcessor;
import scot.mygov.publishing.components.funnelback.postprocess.ResultLinkRewriter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.equalsAny;

@Service
public class FunnelbackService {

    private static final String URL_TEMPLATE
            = "/search.json?query={query}&start_rank={rank}&collection={collection}&profile={profile}";

    List<String> sites = Arrays.asList("www.mygov.scot");

    public FunnelbackSearchResponse getSearchResponse(
            String query,
            boolean qsupOff,
            int rank,
            String requestURL,
            FunnelbackComponentInfo componentInfo) {

        Map<String, Object> params = paramMap(query, rank, componentInfo);
        ResourceServiceBroker broker = CrispHstServices.getDefaultResourceServiceBroker(HstServices.getComponentManager());
        String urlTemplate = getUrlTemplate(qsupOff);
        Resource results = broker.resolve("funnelback", urlTemplate, params);
        ResourceBeanMapper resourceBeanMapper = broker.getResourceBeanMapper("funnelback");
        FunnelbackSearchResponse response = resourceBeanMapper.map(results, FunnelbackSearchResponse.class);
        postProcess(requestURL, response);
        return response;
    }

    String getUrlTemplate(boolean qsupOff) {
        // add the qusup param if it is switched off.  Otherwise the param is omited so that it defaults.
        return qsupOff ? URL_TEMPLATE + "&qsup=off" : URL_TEMPLATE;
    }

    void postProcess(String requestURL, FunnelbackSearchResponse response) {
        PaginationProcessor paginationProcessor = new PaginationProcessor(requestURL);
        paginationProcessor.process(response);

        VirtualHost virtualHost = RequestContextProvider.get().getResolvedMount().getMount().getVirtualHost();
        String hostGroupName = virtualHost.getHostGroupName();
        if (useRewriter(hostGroupName)) {
            PostProcessor postProcessor = new ResultLinkRewriter(virtualHost.getName(), sites);
            postProcessor.process(response);
        }
    }
    boolean useRewriter(String hostGroupName) {
        return !equalsAny(hostGroupName, "production", "dev-localhost");
    }

    Map<String, Object> paramMap(String query, int rank, FunnelbackComponentInfo componentInfo) {
        Map<String, Object> params = new HashMap<>();
        params.put("query", query);
        params.put("rank", rank);
        params.put("profile", componentInfo.getProfile());
        params.put("collection", componentInfo.getCollection());
        return params;
    }

}