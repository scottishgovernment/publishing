package scot.mygov.publishing.components.funnelback;

import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.ComponentConfiguration;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scot.mygov.publishing.beans.Searchsettings;

import javax.servlet.ServletContext;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static scot.mygov.publishing.components.funnelback.SearchResponse.blankSearchResponse;

@Service
@Component("scot.mygov.publishing.components.funnelback.ResilientSearchComponent")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResilientSearchComponent extends EssentialsContentComponent {

    @Autowired
    private FunnelbackSearchService funnelbackSearchService;

    private BloomreachSearchService bloomreachSearchService;

    private ResilientSearchService resilientSearchService;

    private static final String SEARCH_TYPE_RESILIENT = "resilient";

    private static final String SEARCH_TYPE_FUNNELBACK = "funnelback";

    private static final String SEARCH_TYPE_BLOOMREACH = "bloomreach";

    // the type of search that this component is configured to provide in the sitemap
    private String searchType;

    private static Boolean hystrixPropertiesStrategySet = false;

    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) {
        super.init(servletContext, componentConfig);
        bloomreachSearchService = new BloomreachSearchService();
        resilientSearchService = new ResilientSearchService();
        resilientSearchService.setFunnelbackSearchService(funnelbackSearchService);
        resilientSearchService.setBloomreachSearchService(bloomreachSearchService);
        searchType = componentConfig.getRawParameters().getOrDefault("searchtype", SEARCH_TYPE_RESILIENT);
        ensueHystrixPropertiesStrategy();
    }

    private void ensueHystrixPropertiesStrategy() {
        // override the HystrixPropertiesStrategy so that we can set the timeout value at runtime from a value stored
        // in the repo without it being cached
        synchronized (hystrixPropertiesStrategySet) {
            if (!hystrixPropertiesStrategySet.booleanValue()) {
                hystrixPropertiesStrategySet = true;
                HystrixPropertiesStrategy newStrategy = new HystrixPropertiesStrategyWithReloadableCache();
                HystrixPlugins.getInstance().registerPropertiesStrategy(newStrategy);
            }
        }
    }

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        Searchsettings searchsettings = searchSettings(request);
        if (isEnabled(searchsettings) ) {
            Search search = search(request);

            String useSearchType = searchType(searchsettings);
            SearchService searchService = searchService(useSearchType);
            request.setAttribute("enabled", searchsettings.getEnabled().booleanValue());
            request.setAttribute("searchType", useSearchType);

            SearchResponse searchResponse =
                    isBlank(search.getQuery())
                    ? blankSearchResponse()
                    : searchService.performSearch(search, searchsettings);
            populateRequestAttributes(request, searchResponse, searchsettings);
        } else {
            request.setAttribute("enabled", false);
        }
    }

    boolean isEnabled(Searchsettings searchsettings) {
        return  searchsettings != null && searchsettings.getEnabled().booleanValue();
    }

    Search search(HstRequest request) {
        String query = getAnyParameter(request, "q");
        String qsup = getAnyParameter(request, "qsup");
        int page = getAnyIntParameter(request, "page", 1);
        boolean qsupOff = "off".equals(qsup);
        return new SearchBuilder()
                .query(query)
                .page(page)
                .enableSuplimentaryQueries(qsupOff)
                .request(request).build();
    }

    Searchsettings searchSettings(HstRequest request) {
        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
        return baseBean.getBean("administration/search-settings", Searchsettings.class);
    }

    String searchType(Searchsettings searchsettings) {
        // if this component is configured to provide a 'resilient' search then the search type should be the one
        // specified in the searchsettings - this allows us to provide an override.
        return SEARCH_TYPE_RESILIENT.equals(searchType)
                ? searchsettings.getSearchtype()
                : searchType;
    }

    SearchService searchService(String searchtype) {
        switch (searchtype) {
            case SEARCH_TYPE_FUNNELBACK: return funnelbackSearchService;
            case SEARCH_TYPE_BLOOMREACH: return bloomreachSearchService;
            case SEARCH_TYPE_RESILIENT :
            default:
                return resilientSearchService;
        }
    }

    void populateRequestAttributes(HstRequest request, SearchResponse searchResponse, Searchsettings searchsettings) {
        request.setAttribute("searchType", searchResponse.getType().toString());
        request.setAttribute("question", searchResponse.getQuestion());
        request.setAttribute("response", searchResponse.getResponse());
        request.setAttribute("bloomreachresults", searchResponse.getBloomreachResults());
        request.setAttribute("pagination", searchResponse.getPagination());
    }
}
