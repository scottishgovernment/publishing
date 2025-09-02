package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scot.gov.publishing.hippo.funnelback.component.Search;
import scot.gov.publishing.hippo.funnelback.component.SearchResponse;
import scot.gov.publishing.hippo.funnelback.component.SearchService;
import scot.gov.publishing.hippo.funnelback.component.SearchSettings;
import scot.gov.publishing.hippo.funnelback.component.postprocess.PaginationBuilder;
import scot.gov.publishing.hippo.funnelback.model.Pagination;
import scot.gov.publishing.hippo.funnelback.model.Question;
import scot.gov.publishing.hippo.funnelback.model.Response;
import scot.gov.publishing.hippo.funnelback.model.ResultsSummary;

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;
import static org.onehippo.repository.util.JcrConstants.JCR_PRIMARY_TYPE;
import static scot.mygov.publishing.components.ConstraintUtils.fieldConstraints;

@Service
@Component("scot.mygov.publishing.components.BloomreachSearchService")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BloomreachSearchService implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(BloomreachSearchService.class);

    private static final Collection<String> FIELD_NAMES = new ArrayList<>();

    private static final int PAGE_SIZE = 10;

    static String[] NON_PAGE_TYPES = {
            "publishing:analytics",
            "publishing:facebookverification",

            "publishing:smartanswerconfirmationpage",
            "publishing:smartanswermultiplechoiceoption",
            "publishing:smartanswermultiplechoicequestion",
            "publishing:smartanswermultipleselectoption",
            "publishing:smartanswermultipleselectquestion",
            "publishing:smartanswerquestion",
            "publishing:smartanswerresult",
            "publishing:smartanswerresultdynamicselector",
            "publishing:smartanswersingleselectoption",
            "publishing:smartanswersingleselectquestion",

            "publishing:fragment",
            "publishing:mirror",
            "publishing:analytics",
            "publishing:contentcontact",
            "publishing:sector",

            "publishing:featuregriditem",
            "publishing:featureditem",
            "publishing:imageandtext",
            "publishing:navigationcard",
            "publishing:pageheading",
            "publishing:text",
            "publishing:video",

            "publishing:dsexample",

            "robotstxt:robotstxt",
    };

    static {
        Collections.addAll(FIELD_NAMES,
                "publishing:title",
                "publishing:summary",
                "publishing:content/hippostd:content",
                "publishing:contentBlocks/publishing:content/@hippostd:content",
                "hippostd:tags"
        );
    }

    @Override
    public SearchResponse performSearch(Search search, SearchSettings searchsettings) {

        String query = defaultIfBlank(search.getQuery(), "");
        int offset = (search.getPage() - 1) * PAGE_SIZE;

        HstQuery hstQuery = query(search, offset, search.getRequest());
        try {
            HstQueryResult result = hstQuery.execute();
            return response(result, search, query, offset, search.getRequestUrl());
        } catch (QueryException e) {
            LOG.error("Query exceptions in fallback", e);
            return null;
        }
    }

    @Override
    public List<String> getSuggestions(String s, String mount, SearchSettings searchSettings) {
        return emptyList();
    }

    SearchResponse response(HstQueryResult result, Search search, String query, int offset, String url) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setType(SearchResponse.Type.BLOOMREACH);

        Question question = getQuestion(query);
        searchResponse.setQuestion(question);

        Response response = new Response();
        ResultsSummary resultsSummary = buildResultsSummary(result, offset);
        response.getResultPacket().setResultsSummary(resultsSummary);
        searchResponse.setResponse(response);
        response.getResultPacket().setQueryHighlightRegex(query);

        Pagination pagination = new PaginationBuilder().getPagination(resultsSummary, search);
        searchResponse.setPagination(pagination);

        searchResponse.setBloomreachResults(result.getHippoBeans());

        return searchResponse;
    }

    public HstQuery query(Search search, int offset, HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        return HstQueryBuilder
                .create(context.getSiteContentBaseBean())
                .where(whereConstraint(search))
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
        resultsSummary.setCurrEnd(Math.min(resultsSummary.getCurrStart() + PAGE_SIZE, result.getTotalSize()));
        resultsSummary.setNumRanks(PAGE_SIZE);
        resultsSummary.setTotalMatching(result.getTotalSize());
        resultsSummary.setFullyMatching(result.getTotalSize());
        return resultsSummary;
    }

    Constraint whereConstraint(Search search) {
        String query = defaultIfBlank(search.getQuery(), "");
        String parsedQueryStr = SearchInputParsingUtils.parse(query, false);
        List<Constraint> constraints = new ArrayList<>();
        Collections.addAll(constraints, showInParentConstraint(),
                excludeTypesConstraint(),
                ConstraintUtils.topicsConstraint(search),
                ConstraintUtils.publicationTypesConstraint(search),
                termConstraint(parsedQueryStr));
        constraints.addAll(ConstraintUtils.dateConstraint(search));
        return and(constraints.stream().filter(Objects::nonNull).collect(toList()).toArray(new Constraint[0]));
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

}