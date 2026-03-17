package scot.mygov.publishing.components;

import org.apache.commons.beanutils.MethodUtils;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scot.gov.publishing.hippo.search.PaginationBuilder;
import scot.gov.publishing.hippo.search.SearchService;
import scot.gov.publishing.hippo.search.SearchSettings;
import scot.gov.publishing.hippo.search.model.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
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

    private static final int PAGE_SIZE = 10;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    private static final String[] NON_PAGE_TYPES = {
            "publishing:analytics",
            "publishing:facebookverification",

            "publishing:smartanswerconfirmationpage",
            "publishing:smartanswermultiplechoiceoption",
            "publishing:smartanswermultiplechoicequestion",
            "publishing:smartanswermultipleselectoption",
            "publishing:smartanswermultipleselectquestion",
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

    @Override
    public SearchResponse performSearch(Search search, SearchSettings searchsettings) {
        String query = defaultIfBlank(search.getQuery(), "");
        int offset = (search.getPage() - 1) * PAGE_SIZE;

        HstQuery hstQuery = query(search, offset, search.getRequest());
        try {
            HstQueryResult result = hstQuery.execute();
            return response(result, search, query, offset);
        } catch (QueryException e) {
            LOG.error("Query exceptions in fallback", e);
            return SearchResponse.blankSearchResponse();
        } catch (Throwable t) {
            LOG.info("Error performing bloomreach search", t);
            return SearchResponse.blankSearchResponse();
        }
    }

    @Override
    public List<String> getSuggestions(String s, String mount, SearchSettings searchSettings) {
        return emptyList();
    }

    SearchResponse response(HstQueryResult result, Search search, String query, int offset) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setType(SearchResponse.Type.BLOOMREACH);
        Question question = getQuestion(query);
        searchResponse.setQuestion(question);

        ResultsSummary resultsSummary = buildResultsSummary(result, offset);
        searchResponse.setQueryHighlightRegex(query);
        searchResponse.setResultsSummary(resultsSummary);
        Pagination pagination = new PaginationBuilder().getPagination(resultsSummary, search);
        searchResponse.setPagination(pagination);
        searchResponse.setResults(results(result.getHippoBeans(), search.getRequest().getRequestContext()));
        searchResponse.setHasResults(!searchResponse.getResults().isEmpty());
        return searchResponse;
    }

    List<Result> results(HippoBeanIterator it, HstRequestContext requestContext) {
        List<Result> results = new ArrayList<>();
        while (it.hasNext()) {
            HippoBean bean = it.nextHippoBean();
            results.add(result(bean, requestContext));
        }
        return results;
    }

    Result result(HippoBean bean, HstRequestContext requestContext) {
        Result res = new Result();
        res.setLink(link(bean, requestContext));
        res.setSummary(bean.getSingleProperty("publishing:summary"));
        res.setLabel(label(bean));
        HippoBean partOf = partOfLink(bean);
        if (partOf != null) {
            res.getPartOf().add(link(partOf, requestContext));
        }
        scot.mygov.publishing.beans.Image imageBean
                =  bean.getBean("publishing:Image", scot.mygov.publishing.beans.Image.class);
        if (imageBean != null) {
            res.setImage(image(imageBean.getImage(), requestContext));
        }
        Calendar date = bean.getSingleProperty("publishing:displayDate");
        if (date == null) {
            date = bean.getSingleProperty("publishing:publicationDate");
        }
        res.setDisplayDate(date == null ? null : dateTimeFormatter.format(date.toInstant().atZone(java.time.ZoneId.systemDefault())));
        return res;
    }

    Link link(HippoBean bean, HstRequestContext requestContext) {
        HstLinkCreator linkCreator = RequestContextProvider.get().getHstLinkCreator();
        HstLink hstlink = linkCreator.create(bean, RequestContextProvider.get());
        String url = hstlink.toUrlForm(requestContext, false);
        Link link = new Link();
        link.setUrl(url);
        link.setLabel(bean.getSingleProperty("publishing:title"));
        return link;
    }

    Image image(HippoBean imageBean, HstRequestContext requestContext) {
        HstLinkCreator linkCreator = requestContext.getHstLinkCreator();
        String link = linkCreator.create(imageBean, requestContext).toUrlForm(requestContext, true);
        link = link + "/" + imageBean.getName();
        return createImage(link, "publishing");
    }

    public static Image createImage(String img, String prefix) {
        Image image = new Image();
        image.setImage(img + "/" + prefix + "%3Amediumtwocolumnssquare");
        image.getSizes().add(img + "/" + prefix + "%3Amediumtwocolumnssquare 96w");
        image.getSizes().add(img + "/" + prefix + "%3Alargetwocolumnssquare 128w");
        image.getSizes().add(img + "/" + prefix + "%3Amediumtwocolumnsdoubledsquare 192w");
        image.getSizes().add(img + "/" + prefix + "%3Alargetwocolumnsdoubledsquare 256w");
        return image;
    }

    HippoBean partOfLink(HippoBean bean) {
        Method method = MethodUtils.getMatchingAccessibleMethod(bean.getClass(), "getPartOfBean", new Class[0]);
        if (method == null) {
            return null;
        }
        try {
            return (HippoBean) MethodUtils.invokeMethod(bean, "getPartOfBean", new String [] {});
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOG.error("unable to invoke getPartOfBean", e);
            return null;
        }
    }

    String label(HippoBean bean) {
        Method method = MethodUtils.getMatchingAccessibleMethod(bean.getClass(), "getLabel", new Class[0]);
        if (method == null) {
            return null;
        }
        try {
            return (String) MethodUtils.invokeMethod(bean, "getLabel", new String [] {});
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOG.error("unable to invoke getLabel", e);
            return null;
        }
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
        resultsSummary.setCurrEnd(Math.min(resultsSummary.getCurrStart() + PAGE_SIZE - 1, result.getTotalSize()));
        resultsSummary.setNumRanks(PAGE_SIZE);
        resultsSummary.setTotalMatching(result.getTotalSize());
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