package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.repository.util.DateTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.valves.PreviewKeyUtils;

import javax.jcr.RepositoryException;

import java.util.*;

import static java.util.Collections.addAll;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.and;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;
import static scot.mygov.publishing.valves.PreviewKeyUtils.isPreviewMount;

public class FragmentsComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FragmentsComponent.class);

    private static final String FRAGMENT_TYPE = "publishing:fragment";

    private static final String FRAGMENTS = "fragments";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        try {
             populateRequest(request);
        } catch (RepositoryException | QueryException e) {
            LOG.warn("Error trying to fetch fragments", e);
        }
    }

    void populateRequest(HstRequest request) throws RepositoryException, QueryException {
        HippoBean scopeBean = getScopeBean(request);

        // of no scope was specified then dont return any fragments
        if (scopeBean == null) {
            LOG.warn("Fragment request with invalid path specified");
            request.setAttribute(FRAGMENTS, emptyList());
            return;
        }

        // if the base bean is a fragment then we will just return that one fragment
        if (scopeBean.getNode().isNodeType(FRAGMENT_TYPE)) {
            request.setAttribute(FRAGMENTS, singletonList(scopeBean));
            return;
        }

        // run a query to get the bean fragments that match in that folder
        HstQuery query = getQuery(request, scopeBean);
        HstQueryResult result = query.execute();
        request.setAttribute(FRAGMENTS, result.getHippoBeans());
    }

    HippoBean getScopeBean(HstRequest request) {
        String contentPath = substringAfter(request.getPathTranslated(), "/fragments");
        if (isEmpty(contentPath)) {
            return null;
        }
        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();

        // remove the path of the base bean from the path.
        contentPath = substringAfter(contentPath, baseBean.getPath() + "/");
        return baseBean.getBean(contentPath);
    }

    HstQuery getQuery(HstRequest request, HippoBean scopeBean) {
        HstQueryBuilder queryBuilder = HstQueryBuilder.create(scopeBean).ofTypes(FRAGMENT_TYPE).limit(20);
        List<Constraint> constraints = new ArrayList<>();
        addAll(constraints, tagConstraint(request), stagingConstraint(request));
        constraints = constraints.stream().filter(Objects::nonNull).collect(toList());
        if (!constraints.isEmpty()) {
            queryBuilder.where(and(constraints.toArray(new Constraint[constraints.size()])));
        }
        return queryBuilder.build();
    }

    Constraint tagConstraint(HstRequest request) {
        String [] tags = request.getRequestContext().getServletRequest().getParameterValues("tag");
        if (tags == null) {
            return null;
        }
        List<Constraint> constraints = Arrays.stream(tags).distinct().map(this::tagEquals).collect(toList());
        // special case, the query is understood to have an implicit or 'tag=all'
        constraints.add(tagEquals("all"));
        return or(constraints.toArray(new Constraint[] {}));
    }

    Constraint tagEquals(String type) {
        // treat local authorities as if they are tags
        return or(
                constraint("hippostd:tags").equalTo(type),
                constraint("publishing:localauthorities").equalTo(type)
        );
    }

    Constraint stagingConstraint(HstRequest request) {
        // we only want to add this constraint if this is the preview mount
        if (!isPreviewMount(request.getRequestContext())) {
            return null;
        }

        Set<String> keys = previewKeys(request);
        if (keys.isEmpty()) {
            return null;
        }

        Calendar now = Calendar.getInstance();
        List<Constraint> keyConstraints = keys.stream().map(key -> constraintForKey(key, now)).collect(toList());
        return or(keyConstraints.toArray(new Constraint[keyConstraints.size()]));
    }

    Constraint constraintForKey(String key, Calendar now) {
        return and(
                constraint("previewId/@staging:key").equalTo(key),
                constraint("previewId/@staging:expirationdate").greaterOrEqualThan(now, DateTools.Resolution.MILLISECOND)
        );
    }

    Set<String> previewKeys(HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        return PreviewKeyUtils.getPreviewKeys(
                context.getServletRequest(),
                context.getServletResponse(),
                context.getResolvedMount().getMount());
    }
}
