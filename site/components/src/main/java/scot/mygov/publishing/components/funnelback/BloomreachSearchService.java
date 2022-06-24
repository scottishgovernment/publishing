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
import org.hippoecm.hst.util.HstRequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.funnelback.model.FunnelbackSearchResponse;
import scot.gov.publishing.hippo.funnelback.model.Result;
import scot.gov.publishing.hippo.funnelback.model.ResultsSummary;
import scot.mygov.publishing.components.funnelback.postprocess.PaginationProcessor;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.and;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;

import static org.onehippo.repository.util.JcrConstants.JCR_PRIMARY_TYPE;

public class BloomreachSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(BloomreachSearchService.class);

    private static final Collection<String> FIELD_NAMES = new ArrayList<>();

    private static final int PAGE_SIZE = 10;

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

    public FunnelbackSearchResponse getFallbackSearchResponse(HstRequest request, String queryStr, int page) {
        FunnelbackSearchResponse response = initialResponse(queryStr);
        int offset = (page - 1) * PAGE_SIZE;
        HstQuery query = query(queryStr, offset, request);
        try {
            doExecuteQueryAndPopulateResults(query, response, offset);
            addPagination(response, request);
        } catch (QueryException e) {
            response.setError("Unable to perform search");
            LOG.error("Query exceptions in fallback", e);
        }
        return response;
    }

    public HstQuery query(String queryStr, int offset, HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        return HstQueryBuilder
                .create(context.getSiteContentBaseBean())
                .where(whereConstraint(queryStr))
                .limit(PAGE_SIZE)
                .offset(offset)
                .build(context.getQueryManager());
    }

    FunnelbackSearchResponse initialResponse(String queryStr) {
        FunnelbackSearchResponse response = new FunnelbackSearchResponse();
        response.getQuestion().setOriginalQuery(queryStr);
        response.getQuestion().setQuery(queryStr);
        return response;
    }

    void doExecuteQueryAndPopulateResults(HstQuery query, FunnelbackSearchResponse response, int offset) throws QueryException {
        HstQueryResult result = query.execute();
        populateResultsSummary(response, offset, result);
        HippoBeanIterator it = result.getHippoBeans();
        while (it.hasNext()) {
            HippoBean bean = it.nextHippoBean();
            Result res = new Result();
            res.setBean(bean);
            response.getResponse().getResultPacket().getResults().add(res);
        }
    }

    void addPagination(FunnelbackSearchResponse response, HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        HttpServletRequest servletRequest = context.getServletRequest();
        String url = HstRequestUtils.getExternalRequestUrl(servletRequest, false);
        PaginationProcessor paginationProcessor = new PaginationProcessor(url);
        paginationProcessor.process(response);
    }

    void populateResultsSummary(FunnelbackSearchResponse response, int offset, HstQueryResult result) {
        ResultsSummary resultsSummary = response.getResponse().getResultPacket().getResultsSummary();
        resultsSummary.setCurrStart(offset + 1);
        resultsSummary.setCurrEnd(resultsSummary.getCurrStart() + PAGE_SIZE);
        resultsSummary.setNumRanks(PAGE_SIZE);
        resultsSummary.setTotalMatching(result.getTotalSize());
        resultsSummary.setFullyMatching(result.getTotalSize());
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
