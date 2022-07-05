package scot.mygov.publishing.components.funnelback;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.funnelback.model.*;
import scot.mygov.publishing.beans.Searchsettings;
import scot.mygov.publishing.components.funnelback.postprocess.PaginationBuilder;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.and;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.or;

import static org.onehippo.repository.util.JcrConstants.JCR_PRIMARY_TYPE;

public class BloomreachSearchService implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(BloomreachSearchService.class);

    private static final Collection<String> FIELD_NAMES = new ArrayList<>();

    private static final int PAGE_SIZE = 10;

    static String[] NON_PAGE_TYPES = {
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

    @Override
    public SearchResponse performSearch(Search search, Searchsettings searchsettings) {

        String query = defaultIfBlank(search.getQuery(), "");
        int offset = (search.getPage() - 1) * PAGE_SIZE;

        HstQuery hstQuery = query(query, offset, search.getRequest());
        try {
            HstQueryResult result = hstQuery.execute();
            return response(result, query, offset, search.getRequestUrl());
        } catch (QueryException e) {
            LOG.error("Query exceptions in fallback", e);
            return null;
        }
    }

    SearchResponse response(HstQueryResult result, String query, int offset, String url) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setType(SearchResponse.Type.BLOOMREACH);

        Question question = getQuestion(query);
        searchResponse.setQuestion(question);

        Response response = new Response();
        ResultsSummary resultsSummary = buildResultsSummary(result, offset);
        response.getResultPacket().setResultsSummary(resultsSummary);
        searchResponse.setResponse(response);

        Pagination pagination = new PaginationBuilder(url).getPagination(resultsSummary, query);
        searchResponse.setPagination(pagination);

        searchResponse.setBloomreachResults(result.getHippoBeans());

        return searchResponse;
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

    Question getQuestion(String query) {
        Question question = new Question();
        question.setOriginalQuery(query);
        question.setQuery(query);
        return question;
    }

    ResultsSummary buildResultsSummary(HstQueryResult result, int offset) {
        ResultsSummary resultsSummary = new ResultsSummary();
        resultsSummary.setCurrStart(offset + 1);
        resultsSummary.setCurrEnd(resultsSummary.getCurrStart() + PAGE_SIZE);
        resultsSummary.setNumRanks(PAGE_SIZE);
        resultsSummary.setTotalMatching(result.getTotalSize());
        resultsSummary.setFullyMatching(result.getTotalSize());
        return resultsSummary;
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
