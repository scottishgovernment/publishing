package scot.mygov.publishing.components.funnelback;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.util.HstRequestUtils;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scot.gov.publishing.hippo.funnelback.model.FunnelbackSearchResponse;

import javax.servlet.http.HttpServletRequest;

@Service
@Component("scot.mygov.publishing.components.funnelback.SearchComponent")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ParametersInfo(type = FunnelbackComponentInfo.class)
public class SearchComponent extends EssentialsContentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SearchComponent.class);

    @Autowired
    private FunnelbackService funnelbackService;

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        String query = param(request, "q", "");
        String qsup = param(request, "qsup", "");
        int rank = getRank(request);
        FunnelbackComponentInfo componentInfo = getComponentParametersInfo(request);
        HttpServletRequest servletRequest = request.getRequestContext().getServletRequest();
        String url = HstRequestUtils.getExternalRequestUrl(servletRequest, false);

        // the qsup paramater should either be 'off' to disable or omited from the URL to use the default configured
        // query blending
        boolean qsupOff = "off".equals(qsup);
        FunnelbackSearchResponse funnelbackSearchResponse
                = funnelbackService.getSearchResponse(query, qsupOff, rank, url, componentInfo);
        request.setAttribute("response", funnelbackSearchResponse.getResponse());
        request.setAttribute("question", funnelbackSearchResponse.getQuestion());
    }

    int getRank(HstRequest request) {
        String pageString = param(request, "page", "1");
        try {
            // the rank used by funnelback is 1 based
            // page is one based
            int page = Integer.parseInt(pageString);
            return ((page - 1) * 10) + 1;
        } catch (NumberFormatException e) {
            LOG.warn("Invalid page number {}", pageString, e);
            return 1;
        }
    }

    String getFunnelbackEnvFromMount(HstRequest request) {
        Mount mount = request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getResolvedMount()
                .getMount();
        return mount.getProperty("publishing:funnelbackenv");
    }

    private String param(HstRequest request, String param, String defaultValue) {
        String paramValue = request
                .getRequestContext()
                .getServletRequest()
                .getParameter(param);
        return StringUtils.isBlank(paramValue) ? defaultValue : paramValue;
    }
}