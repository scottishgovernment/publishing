package scot.mygov.publishing.components;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.ComponentConfiguration;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.onehippo.cms7.essentials.components.EssentialsListComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsListComponentInfo;

import javax.servlet.ServletContext;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.and;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;

@ParametersInfo(type = EssentialsListComponentInfo.class)
public class SearchComponent extends EssentialsListComponent {

    private static final Collection<String> FIELD_NAMES = new ArrayList<>();

    @Override
    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) {

        super.init(servletContext, componentConfig);
        Collections.addAll(FIELD_NAMES,
                "publishing:title",
                "publishing:summary",
                "publishing:content/hippostd:content",
                "publishing:tags"
        );
    }

    @Override
    protected <T extends EssentialsListComponentInfo>
    HstQuery buildQuery(final HstRequest request, final T paramInfo, final HippoBean scope) {

        final int pageSize = getPageSize(request, paramInfo);
        final int page = getCurrentPage(request);
        final int offset = (page - 1) * pageSize;
        HstQueryBuilder builder = HstQueryBuilder.create(request.getRequestContext().getSiteContentBaseBean());
        HippoBean siteFurniture = scope.getBean("site-furniture");
        HippoBean administration = scope.getBean("administration");
        return builder
                .excludeScopes(siteFurniture, administration)
                .where(constraints(request))
                .limit(pageSize)
                .offset(offset)
                .build();
    }

    private Constraint constraints(HstRequest request) {
        List<Constraint> constraints = new ArrayList<>();
        addTermConstraints(constraints, request);
        return and(constraints.toArray(new Constraint[] {}));
    }

    private void addTermConstraints(List<Constraint> constraints, HstRequest request) {
        String term = param(request, "q");
        if (StringUtils.isBlank(term)) {
            return;
        }

        String parsedTerm = SearchInputParsingUtils.parse(term, false);
        constraints.add(or(fieldConstraints(parsedTerm)));
    }

    private Constraint [] fieldConstraints(String term) {
        return FIELD_NAMES.stream()
                .map(field -> constraint(field).contains(term))
                .collect(toList())
                .toArray(new Constraint[FIELD_NAMES.size()]);
    }

    private String param(HstRequest request, String param) {
        return request
                .getRequestContext()
                .getServletRequest()
                .getParameter(param);
    }

}
