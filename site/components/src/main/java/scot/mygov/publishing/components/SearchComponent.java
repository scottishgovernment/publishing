package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.onehippo.cms7.essentials.components.EssentialsListComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsListComponentInfo;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.and;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;
import static org.onehippo.repository.util.JcrConstants.JCR_PRIMARY_TYPE;

@ParametersInfo(type = EssentialsListComponentInfo.class)
public class SearchComponent extends EssentialsListComponent {

    private static final Collection<String> FIELD_NAMES = new ArrayList<>();

    static {
        Collections.addAll(FIELD_NAMES,
                "publishing:title",
                "publishing:summary",
                "publishing:content/hippostd:content",
                "hippostd:tags"
        );
    }

    @Override
    protected <T extends EssentialsListComponentInfo>
    HstQuery buildQuery(final HstRequest request, final T paramInfo, final HippoBean scope) {

        final int pageSize = getPageSize(request, paramInfo);
        final int page = getCurrentPage(request);
        final int offset = (page - 1) * pageSize;
        return HstQueryBuilder
                .create(request.getRequestContext().getSiteContentBaseBean())
                .where(whereConstraint(request))
                .limit(pageSize)
                .offset(offset)
                .build();
    }

    Constraint whereConstraint(HstRequest request) {
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(showInParentConstraint());
        constraints.add(excludeTypesConstraint());
        String term = param(request, "q");
        String parsedTerm = SearchInputParsingUtils.parse(term, false);
        if (!isBlank(parsedTerm)) {
            constraints.add(termConstraint(parsedTerm));
        }

        return and(constraints.toArray(new Constraint[] {}));
    }

    Constraint showInParentConstraint() {
        // only include content if the showInParentFlag has not been set to false.
        return or(
                constraint("publishing:showInParent").equalTo(true),
                constraint("publishing:showInParent").notExists()
        );
    }

    Constraint excludeTypesConstraint() {
        return and(
                notType("publishing:mirror"),
                notType("publishing:documentcoverpage"),
                notType("publishing:notificationbanner"),
                notType("publishing:home"),
                notType("publishing:category"),
                notType("publishing:organisationlist"),
                notType("publishing:contentcontact"),
                notType("publishing:sector"),
                notType("publishing:status"),
                notType("publishing:search"),
                notType("publishing:cookiepage"),
                notType("publishing:analytics"),
                notType("publishing:facebookverification"),
                notType("robotstxt:robotstxt")
        );
    }

    Constraint notType(String type) {
        return constraint(JCR_PRIMARY_TYPE).notEqualTo(type);
    }

    private Constraint termConstraint(String term) {
        return or(fieldConstraints(term));
    }

    private Constraint [] fieldConstraints(String term) {
        return FIELD_NAMES.stream()
                .map(field -> fieldConstraint(field, term))
                .collect(toList())
                .toArray(new Constraint[FIELD_NAMES.size()]);
    }

    Constraint fieldConstraint(String field, String term) {
        return constraint(field).contains(term);
    }

    private String param(HstRequest request, String param) {
        return request
                .getRequestContext()
                .getServletRequest()
                .getParameter(param);
    }

}
