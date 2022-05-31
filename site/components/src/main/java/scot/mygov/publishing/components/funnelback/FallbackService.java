package scot.mygov.publishing.components.funnelback;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.components.funnelback.model.FunnelbackSearchResponse;
import scot.mygov.publishing.components.funnelback.model.Result;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.and;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;
import static org.onehippo.repository.util.JcrConstants.JCR_PRIMARY_TYPE;

/**
 * Bloomreach search service used if funnelback time out or returns an error.
 */
public class FallbackService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchComponent.class);

    private static final Collection<String> FIELD_NAMES = new ArrayList<>();

    static String [] NON_PAGE_TYPES = {
            "publishing:analytics",
            "publishing:facebookverification",
            "publishing:smartanswerquestion",
            "publishing:smartanswermultiplechoicequestion",
            "publishing:smartanswerresult",
            "publishing:fragment",
            "publishing:mirror",
            "publishing:analytics",
            "publishing:contentcontact",
            "publishing:sector",
            "robotstxt:robotstxt",
    };

    static {
        Collections.addAll(FIELD_NAMES,
                "publishing:title",
                "publishing:summary",
                "publishing:content/hippostd:content",
                "hippostd:tags"
        );
    }

    public FunnelbackSearchResponse getSearchResponse(HstRequest request, String queryStr, String pageStr) {

        FunnelbackSearchResponse response = new FunnelbackSearchResponse();
        request.setAttribute("implementation", "fallback");
        LOG.info("using fallback implementation");

        int pageSize = 10;
        int page = Integer.parseInt(pageStr);
        final int offset = (page - 1) * pageSize;
        HstRequestContext context = request.getRequestContext();
        HstQuery query = HstQueryBuilder
                .create(context.getSiteContentBaseBean())
                .where(whereConstraint(queryStr))
                .limit(pageSize)
                .offset(offset)
                .build(context.getQueryManager());

        try {
            HstQueryResult result = query.execute();
            HippoBeanIterator it = result.getHippoBeans();
            while (it.hasNext()) {
                HippoBean bean = it.nextHippoBean();
                Result res = new Result();
                res.setBean(bean);
                response.getResponse().getResultPacket().getResults().add(res);
            }

            // TODO: this is juts a PoC, would have to add paginations, message at top of page etc.
        } catch (QueryException e) {
            response.setError("Unable to perform search");
            LOG.error("Query exceptions in fallback", e);
        } catch (Throwable e) {
            LOG.error("something wrong", e);
        }

        return response;
    }

    Constraint whereConstraint(String term) {
        return and(new Constraint [] {
                showInParentConstraint(),
                excludeTypesConstraint(),
                termConstraint(term)
        });
    }

    Constraint showInParentConstraint() {
        // only include content if the showInParentFlag has not been set to false.
        return or(
                constraint("publishing:showInParent").equalTo(true),
                constraint("publishing:showInParent").notExists()
        );
    }

    Constraint excludeTypesConstraint() {

        // exclude document types that are not pages ...
        List<Constraint> constraints
                = Arrays.stream(NON_PAGE_TYPES).map(this::notType).collect(toList());

        // also exclude other types of pages we do not want in the search results
        Collections.addAll(constraints,
                notType("publishing:documentcoverpage"),
                notType("publishing:home"),
                notType("publishing:category"),
                notType("publishing:organisationlist"),
                notType("publishing:status"),
                notType("publishing:search"),
                notType("publishing:cookiepage"));

        return and(constraints.toArray(new Constraint [0]));
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

}
