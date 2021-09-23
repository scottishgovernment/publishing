package scot.mygov.publishing.components;

import org.apache.commons.lang.StringUtils;
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
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;

public class FragmentsComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FragmentsComponent.class);

    private static final String FRAGMENT_TYPE = "publishing:fragment";

    private static final String FRAGMENTS = "fragments";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        try {
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
            HstQuery query = buildQuery(request, scopeBean);
            HstQueryResult result = query.execute();
            request.setAttribute(FRAGMENTS, result.getHippoBeans());
        } catch (RepositoryException | QueryException e) {
            LOG.warn("Error trying to fetch fragments", e);
        }
    }

    HippoBean getScopeBean(HstRequest request) {
        String contentPath = substringAfter(request.getPathTranslated(), "/fragments");
        if (isEmpty(contentPath)) {
            return null;
        }
        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();

        // remove the path of the base bean from the path.
        contentPath = StringUtils.substringAfter(contentPath, baseBean.getPath() + "/");

        return baseBean.getBean(contentPath);
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
        // treat local authorities as if they are tags
        return or(
                constraint("hippostd:tags").equalTo(type),
                constraint("publishing:localauthorities").equalTo(type)
        );
    }

}
