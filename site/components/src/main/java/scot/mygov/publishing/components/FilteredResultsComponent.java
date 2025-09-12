package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.onehippo.cms7.essentials.components.EssentialsListComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsListComponentInfo;
import org.onehippo.cms7.essentials.components.paging.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.funnelback.component.*;
import scot.gov.publishing.hippo.funnelback.component.postprocess.PaginationBuilder;
import scot.gov.publishing.hippo.funnelback.model.Pagination;
import scot.gov.publishing.hippo.funnelback.model.Question;
import scot.gov.publishing.hippo.funnelback.model.Response;
import scot.gov.publishing.hippo.funnelback.model.ResultsSummary;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static scot.mygov.publishing.components.ConstraintUtils.fieldConstraints;

@ParametersInfo(type = FilteredResultsComponentInfo.class)
public class FilteredResultsComponent extends EssentialsListComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FilteredResultsComponent.class);

    private static final int PAGE_SIZE = 10;

    private static final String PUBLICATION_DATE = "publishing:publicationDate";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        setContentBeanWith404(request, response);
        Search search = search(request);
        request.setAttribute("search", search);
        request.setAttribute("filterButtons", FilterButtonGroups.filterButtonGroups(search));
        request.setAttribute("showFilters", true);
        FilteredResultsComponentInfo info = getComponentParametersInfo(request);
        request.setAttribute("displayTypeLabel", info.getDisplayTypeLabel());
    }

    Search search(HstRequest request) {
        String query = param(request, "q");
        int page = getCurrentPage(request);
        LocalDate begin = date(request, "begin");
        LocalDate end = date(request, "end");
        SearchBuilder searchBuilder = new SearchBuilder()
                .query(query)
                .page(page)
                .fromDate(begin)
                .toDate(end)
                .request(request);
        addTopics(request, searchBuilder);
        addPublicationTypes(request, searchBuilder);
        searchBuilder.sort(sort(request));
        return searchBuilder.build();
    }

    Sort sort(HstRequest request) {
        String sortParam = getAnyParameter(request, "sort");
        if (isBlank(sortParam)) {
            /// if there is no sort param then default to the one configured for this page
            FilteredResultsComponentInfo info = getComponentParametersInfo(request);
            sortParam = info.getDefaultSort();
        }

        try {
            return Sort.valueOf(sortParam.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid sort value {}, defaulting to date", sortParam, e);
            return Sort.DATE;
        }
    }

    void addTopics(HstRequest request, SearchBuilder searchBuilder) {
        String [] topics = request.getParameterMap().get("topic");
        if (topics == null) {
            return;
        }
        Map<String, String> topicsMap = new TopicsProvider().get(request.getRequestContext());
        getTopics(request).stream().forEach(topic -> searchBuilder.topics(topic, topicsMap));
    }

    static List<String> getTopics(HstRequest request) {
        String [] topics = request.getParameterMap().get("topic");
        if (topics == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(topics);
    }

    void addPublicationTypes(HstRequest request, SearchBuilder searchBuilder) {
        String [] publicationTypes = request.getParameterMap().get("type");
        if (publicationTypes == null) {
            return;
        }
        Map<String, String> publicationTypesMap = new PublicationTypesProvider(true).get(request.getRequestContext());
        getPublicationTypes(request).stream().forEach(type -> searchBuilder.publicationTypes(type, publicationTypesMap));
    }

    static List<String> getPublicationTypes(HstRequest request) {
        String [] types = request.getParameterMap().get("type");
        if (types == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(types);
    }

    static String param(HstRequest request, String param) {
        HstRequestContext requestContext = request.getRequestContext();
        HttpServletRequest servletRequest = requestContext.getServletRequest();
        return servletRequest.getParameter(param);
    }

    static LocalDate date(HstRequest request, String dateParam) {
        String dateValue = param(request, dateParam);
        if (isBlank(dateValue)) {
            return null;
        }
        return LocalDate.parse(dateValue, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    protected <T extends EssentialsListComponentInfo>
    Pageable<HippoBean> executeQuery(HstRequest request, T paramInfo, HstQuery query) throws QueryException {
        int pageSize = getPageSize(request, paramInfo);
        int page = getCurrentPage(request);
        HstQueryResult queryResult = query.execute();
        populateResponse(request, queryResult);
        return getPageableFactory().createPageable(
                queryResult.getHippoBeans(),
                queryResult.getTotalSize(),
                pageSize,
                page);
    }

    void populateResponse(HstRequest request, HstQueryResult queryResult) {
        Search search = search(request);
        SearchResponse searchResponse = response(queryResult, search);
        request.setAttribute("question", searchResponse.getQuestion());
        request.setAttribute("response", searchResponse.getResponse());
        request.setAttribute("pagination", searchResponse.getPagination());
        request.setAttribute("filterButtons", FilterButtonGroups.filterButtonGroups(search));
    }

    public static SearchResponse response(HstQueryResult result, Search search) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setType(SearchResponse.Type.BLOOMREACH);

        Question question = getQuestion(search.getQuery());
        searchResponse.setQuestion(question);

        Response response = new Response();
        int offset = (search.getPage() - 1) * PAGE_SIZE;
        ResultsSummary resultsSummary = buildResultsSummary(result, offset);
        response.getResultPacket().setResultsSummary(resultsSummary);
        searchResponse.setResponse(response);

        Pagination pagination = new PaginationBuilder().getPagination(resultsSummary, search);
        searchResponse.setPagination(pagination);
        searchResponse.setBloomreachResults(result.getHippoBeans());

        return searchResponse;
    }

    static Question getQuestion(String query) {
        Question question = new Question();
        question.setOriginalQuery(query);
        question.setQuery(query);
        return question;
    }

    public static ResultsSummary buildResultsSummary(HstQueryResult result, int offset) {
        ResultsSummary resultsSummary = new ResultsSummary();
        resultsSummary.setCurrStart(offset + 1);
        resultsSummary.setCurrEnd(Math.min(resultsSummary.getCurrStart() + PAGE_SIZE, result.getTotalSize()));
        resultsSummary.setNumRanks(PAGE_SIZE);
        resultsSummary.setTotalMatching(result.getTotalSize());
        resultsSummary.setFullyMatching(result.getTotalSize());
        return resultsSummary;
    }

    @Override
    protected <T extends EssentialsListComponentInfo>
    HstQuery buildQuery(final HstRequest request, final T paramInfo, final HippoBean scope) {
        int pageSize = getPageSize(request, paramInfo);
        int page = getCurrentPage(request);
        int offset = (page - 1) * pageSize;
        HippoBean scopeFolder = scope.isHippoFolderBean() ? scope : scope.getParentBean();
        HstQueryBuilder builder = HstQueryBuilder.create(scopeFolder);
        Search search = search(request);
        String documentTypesString = paramInfo.getDocumentTypes();
        String [] documentTypes = documentTypesString.split(";");
        HstQueryBuilder queryBuilder = builder.ofTypes(documentTypes)
                .where(constraints(search))
                .limit(pageSize)
                .offset(offset);
        addOrderBy(queryBuilder, search.getSort());
        return queryBuilder.build();
    }

    void addOrderBy(HstQueryBuilder queryBuilder, Sort sort) {
        String [] dateFields = { PUBLICATION_DATE };
        String title = "publishing:title";
        switch (sort) {
            case TITLE:
                queryBuilder.orderBy(HstQueryBuilder.Order.ASC, title);
                break;

            case ADATE:
                queryBuilder.orderBy(HstQueryBuilder.Order.ASC, dateFields);
                queryBuilder.orderBy(HstQueryBuilder.Order.ASC, title);
                break;

            case DATE:
            default:
                queryBuilder.orderBy(HstQueryBuilder.Order.DESC, dateFields);
                queryBuilder.orderBy(HstQueryBuilder.Order.ASC, title);
                break;
        }
    }

    private Constraint constraints(Search search) {
        List<Constraint> constraints = new ArrayList<>();
        addTermConstraints(constraints, search);
        constraints.add(ConstraintUtils.topicsConstraint(search));
        constraints.add(ConstraintUtils.publicationTypesConstraint(search));
        constraints.addAll(ConstraintUtils.dateConstraint(search));
        constraints = constraints.stream().filter(Objects::nonNull).collect(toList());
        return ConstraintBuilder.and(constraints.toArray(new Constraint[] {}));
    }

    private void addTermConstraints(List<Constraint> constraints, Search search) {
        String parsedTerm = SearchInputParsingUtils.parse(search.getQuery(), false);
        if (isBlank(parsedTerm)) {
            return;
        }
        constraints.add(ConstraintBuilder.or(fieldConstraints(parsedTerm)));
    }

}