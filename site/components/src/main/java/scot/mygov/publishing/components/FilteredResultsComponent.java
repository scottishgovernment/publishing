package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.ComponentConfiguration;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.hippoecm.repository.util.DateTools;
import org.onehippo.cms7.essentials.components.EssentialsListComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsListComponentInfo;
import org.onehippo.cms7.essentials.components.paging.Pageable;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.util.SelectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.funnelback.component.*;
import scot.gov.publishing.hippo.funnelback.component.postprocess.PaginationBuilder;
import scot.gov.publishing.hippo.funnelback.model.Pagination;
import scot.gov.publishing.hippo.funnelback.model.Question;
import scot.gov.publishing.hippo.funnelback.model.Response;
import scot.gov.publishing.hippo.funnelback.model.ResultsSummary;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.*;

@ParametersInfo(type = FilteredResultsComponentInfo.class)
public class FilteredResultsComponent extends EssentialsListComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FilteredResultsComponent.class);

    private static final int PAGE_SIZE = 10;

    private static final String PUBLICATION_DATE = "publishing:publicationDate";

    private static final String PUBLICATION_VALUE_LIST = "/content/documents/publishing/valuelists/publicationTypes/publicationTypes";

    private Collection<String> fieldNames = new ArrayList<>();

    @Override
    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) {
        super.init(servletContext, componentConfig);
        Collections.addAll(fieldNames, "publishing:title",
                "publishing:summary",
                "hippostd:tags",
                "publishing:contentBlocks/publishing:content/hippostd:content",
                "publishing:contentBlocks/publishing:document/hippo:filename",
                "publishing:contentBlocks/publishing:title");
    }

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        setContentBeanWith404(request, response);
        Search search = search(request);
        request.setAttribute("search", search);
        request.setAttribute("filterButtons", FilterButtonGroups.filterButtonGroups(search));
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
        Map<String, String> topicsMap = topicsMap(request.getRequestContext());
        getTopics(request).stream().forEach(topic -> searchBuilder.topics(topic, topicsMap));
    }

    static List<String> getTopics(HstRequest request) {
        String [] topics = request.getParameterMap().get("topic");
        if (topics == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(topics);
    }

    public static Map<String, String> topicsMap(HstRequestContext context) {
        HippoBean baseBean = context.getSiteContentBaseBean();
        HippoBean topicsList = getTopicsList(baseBean);
        if (topicsList == null) {
            return emptyMap();
        }
        return SelectionUtil.valueListAsMap((ValueList) topicsList);
    }

    static HippoBean getTopicsList(HippoBean baseBean) {
        HippoFolder administration = baseBean.getBean("administration");
        if (administration == null) {
            return null;
        }

        return administration.getBean("topics");
    }

    void addPublicationTypes(HstRequest request, SearchBuilder searchBuilder) {
        String [] publicationTypes = request.getParameterMap().get("type");
        if (publicationTypes == null) {
            return;
        }
        Map<String, String> publicationTypesMap = publicationTypesMap(request.getRequestContext());
        getPublicationTypes(request).stream().forEach(type -> searchBuilder.publicationTypes(type, publicationTypesMap));
    }

    static List<String> getPublicationTypes(HstRequest request) {
        String [] types = request.getParameterMap().get("type");
        if (types == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(types);
    }

    public static Map<String, String> publicationTypesMap(HstRequestContext context) {
        try {
            Session session = context.getSession();
            if (!session.nodeExists(PUBLICATION_VALUE_LIST)) {
                return Collections.emptyMap();
            }

            Node publicationValueList = session.getNode(PUBLICATION_VALUE_LIST);
            Node typesForSiteNode = publicationTypesForSiteNode(context);
            if (typesForSiteNode == null) {
                return Collections.emptyMap();
            }

            NodeIterator it = publicationValueList.getNodes("selection:listitem");
            Map<String, String> map = new TreeMap<>();
            while (it.hasNext()) {
                Node node = it.nextNode();
                String key = node.getProperty("selection:key").getString();
                if (typesForSiteNode.hasProperty(key)) {
                    map.put(key, node.getProperty("selection:label").getString());
                }
            }
            return map;
        } catch (RepositoryException e) {
            LOG.error("Failed to load publication types", e);
            return Collections.emptyMap();
        }
    }

    static Node publicationTypesForSiteNode(HstRequestContext context) throws RepositoryException {
        Node typesForSiteNode = context.getSession().getNode("/content/publicationtypes");
        String sitename = context.getSiteContentBaseBean().getName();
        return typesForSiteNode.hasNode(sitename) ? typesForSiteNode.getNode(sitename) : null;
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
        HstQueryBuilder queryBuilder = builder.ofTypes(paramInfo.getDocumentTypes())
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
        addTopicsConstraint(constraints, search);
        addPublicationTypesConstraint(constraints, search);
        addDateConstraint(constraints, search);
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

    Constraint [] fieldConstraints(String term) {
        List<Constraint> constraints = fieldNames
                .stream()
                .map(field -> ConstraintBuilder.constraint(field).contains(term))
                .collect(toList());
        return constraints.toArray(new Constraint[constraints.size()]);
    }

    private void addTopicsConstraint(List<Constraint> constraints, Search search) {
        if (search.getTopics().isEmpty()) {
            return;
        }

        Constraint [] constraintsArray = search.getTopics().keySet()
                .stream().map(this::topicConstraint).map(this::orConstraint).toArray(Constraint[]::new);
        constraints.add(ConstraintBuilder.or(constraintsArray));
    }

    private void addPublicationTypesConstraint(List<Constraint> constraints, Search search) {
        if (search.getPublicationTypes().isEmpty()) {
            return;
        }

        Constraint [] constraintsArray = search.getPublicationTypes().keySet()
                .stream().map(this::publicationTypeConstraint).map(this::orConstraint).toArray(Constraint[]::new);
        constraints.add(ConstraintBuilder.or(constraintsArray));
    }

    Constraint orConstraint(Constraint constraint) {
        return or(constraint);
    }

    Constraint publicationTypeConstraint(String topic) {
        return constraint("publishing:publicationType").equalTo(topic);
    }

    Constraint topicConstraint(String topic) {
        return constraint("publishing:topics").equalTo(topic);
    }

    public static void addDateConstraint(List<Constraint> constraints, Search search) {

        if (search.getToDate() != null && search.getFromDate() != null) {
            Calendar beginCal = getCalendar(search.getFromDate());
            Calendar endCal = getCalendar(search.getToDate());
            constraints.add(constraint(PUBLICATION_DATE).between(beginCal, endCal, DateTools.Resolution.DAY));
        }

        addBeginFilter(constraints, search);
        addEndFilter(constraints, search);
    }

    static void addBeginFilter(List<Constraint> constraints, Search search) {
        if (search.getFromDate() == null ) {
            return;
        }

        Calendar calendar = getCalendar(search.getFromDate());
        constraints.add(constraint(PUBLICATION_DATE).greaterOrEqualThan(calendar, DateTools.Resolution.DAY));
    }

    static void addEndFilter(List<Constraint> constraints, Search search) {
        if (search.getToDate() == null ) {
            return;
        }

        Calendar calendar = getCalendar(search.getToDate());
        constraints.add(constraint(PUBLICATION_DATE).lessOrEqualThan(calendar, DateTools.Resolution.DAY));
    }

    static  Calendar getCalendar(LocalDate localDate) {
        return GregorianCalendar.from(localDate.atStartOfDay(ZoneId.systemDefault()));
    }
}