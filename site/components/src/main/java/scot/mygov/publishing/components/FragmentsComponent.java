package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;

/**
 * Fetch fragments from the specified localtion.
 *
 * If a tag params are provider then the fragments are matched such that any fragment with on of the tags is returned,
 * in addition to any that are tagged as 'all'
 */
public class FragmentsComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FragmentsComponent.class);

    private static final String FRAGMENT_TYPE = "publishing:Fragment";

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        try {
            HippoBean scopeBean = getScopeBean(request);

            // of no scope was specified then dont return any fragments
            if (scopeBean == null) {
                LOG.warn("Fragment request with invalid path specified");
                request.setAttribute("fragments", emptyList());
                return;
            }

            // if the base bean is a fragment then we will just return that one fragment
            if (scopeBean.getNode().isNodeType(FRAGMENT_TYPE)) {
                request.setAttribute("fragments", singletonList(scopeBean));
                return;
            }

            // run a query to get the bean fragments that match in that folder
            request.setAttribute("fragments", runQuery(request, scopeBean));
        } catch (RepositoryException | QueryException e) {
            LOG.warn("Error trying to fetch fragments", e);
        }
    }

    HippoBean getScopeBean(HstRequest request) {
        String contentPath = substringAfter(request.getPathTranslated(), "/fragments/");
        if (isEmpty(contentPath)) {
            return null;
        }
        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
        return baseBean.getBean(contentPath);
    }

    HippoBeanIterator runQuery(HstRequest request, HippoBean scopeBean) throws QueryException {
        HstQuery query = buildQuery(request, scopeBean);
        LOG.info("fragment query: {}", query);
        HstQueryResult result = query.execute();
        return result.getHippoBeans();
    }

    HstQuery buildQuery(HstRequest request, HippoBean baseBean) {
        HstQueryBuilder queryBuilder =
                HstQueryBuilder.create(baseBean)
                        .ofTypes(FRAGMENT_TYPE)
                        // what should the limit actually ne?
                        .limit(20);
        addTagConstraint(queryBuilder, request);
        return queryBuilder.build();
    }

    void addTagConstraint(HstQueryBuilder queryBuilder, HstRequest request) {
        String [] tags = request.getRequestContext().getServletRequest().getParameterValues("tag");
        if (tags == null) {
            return;
        }
        List<Constraint> constraints = Arrays.stream(tags).distinct().map(this::tagEquals).collect(toList());

        // special case, the query is understood to have an implicit or 'tag=all'
        constraints.add(tagEquals("all"));
        queryBuilder.where(or(constraints.toArray(new Constraint[] {})));
    }

    Constraint tagEquals(String type) {
        return constraint("hippostd:tags").equalTo(type);
    }

}
