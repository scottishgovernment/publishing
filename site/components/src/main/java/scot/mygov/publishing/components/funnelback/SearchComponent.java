package scot.mygov.publishing.components.funnelback;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scot.mygov.publishing.components.funnelback.model.FunnelbackSearchResponse;

@Service
@Component("scot.mygov.publishing.components.funnelback.SearchComponent")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SearchComponent extends EssentialsContentComponent {

    @Autowired
    private FunnelbackService funnelbackService;

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        // if there is a search then call funnelback

        String query = param(request, "query", "");
        if (StringUtils.isBlank(query)) {
            query = param(request, "q", "");
        }
        String page = param(request, "page", "1");
        if (StringUtils.isNotBlank(query)) {
            FunnelbackSearchResponse funnelbackSearchResponse = funnelbackService.getSearchResponse(request, query, page);
            request.setAttribute("response", funnelbackSearchResponse.getResponse());
        }
    }

    private String param(HstRequest request, String param, String defaultValue) {
        String paramValue = request
                .getRequestContext()
                .getServletRequest()
                .getParameter(param);
        return StringUtils.isBlank(paramValue) ? defaultValue : paramValue;
    }
}
