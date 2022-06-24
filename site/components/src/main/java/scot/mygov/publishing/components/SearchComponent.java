package scot.mygov.publishing.components;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.onehippo.cms7.essentials.components.EssentialsListComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsListComponentInfo;
import scot.mygov.publishing.components.funnelback.BloomreachSearchService;

import static org.apache.commons.lang.StringUtils.isBlank;

@ParametersInfo(type = EssentialsListComponentInfo.class)
public class SearchComponent extends EssentialsListComponent {

    @Override
    protected <T extends EssentialsListComponentInfo>
    HstQuery buildQuery(final HstRequest request, final T paramInfo, final HippoBean scope) {

        String term = param(request, "q", "");
        String parsedTerm = SearchInputParsingUtils.parse(term, false);
        if (isBlank(parsedTerm)) {
            return null;
        }

        final int pageSize = getPageSize(request, paramInfo);
        final int page = getCurrentPage(request);
        final int offset = (page - 1) * pageSize;

        return new BloomreachSearchService().query(parsedTerm, offset, request);
    }

    public static String param(HstRequest request, String param, String defaultValue) {
        String paramValue = request
                .getRequestContext()
                .getServletRequest()
                .getParameter(param);
        return StringUtils.isBlank(paramValue) ? defaultValue : paramValue;
    }
}
