package scot.mygov.publishing.components.funnelback;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import scot.mygov.publishing.beans.Searchsettings;

@Service
@Component("scot.mygov.publishing.components.funnelback.ResilientSearchService")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResilientSearchService implements SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(ResilientSearchService.class);

    private static final String FUNNELBACK_COMMAND_KEY = "FunnelbackCommandKey";

    private FunnelbackSearchService funnelbackSearchService;

    private BloomreachSearchService bloomreachSearchService;

    @Override
    public SearchResponse performSearch(Search search, Searchsettings searchsettings) {
        LOG.info("performSearch {}, {}, {}", searchsettings.getEnabled(), searchsettings.getSearchtype(), searchsettings.getTimeoutMillis());

        HystrixCommandProperties.Setter commandPropertiesSetter = HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(searchsettings.getTimeoutMillis().intValue());
        SearchCommand command = new SearchCommand(search, searchsettings, commandPropertiesSetter);
        return command.execute();
    }

    class SearchCommand extends HystrixCommand<SearchResponse> {

        Search search;

        Searchsettings searchsettings;

        public SearchCommand(Search search, Searchsettings searchsettings, HystrixCommandProperties.Setter commandPropertiesSetter) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(FUNNELBACK_COMMAND_KEY))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("searchWithTimeout"))
                    .andCommandPropertiesDefaults(commandPropertiesSetter));

            this.search = search;
            this.searchsettings = searchsettings;
        }

        @Override
        protected SearchResponse run() {
            LOG.info("Using funnelback search ... {}", search.getQuery());
            return funnelbackSearchService.performSearch(search, searchsettings);
        }

        @Override
        protected SearchResponse getFallback() {
            LOG.info("Using fallback search ... {}", search.getQuery());
            return bloomreachSearchService.performSearch(search, searchsettings);
        }
    }

    public FunnelbackSearchService getFunnelbackSearchService() {
        return funnelbackSearchService;
    }

    public void setFunnelbackSearchService(FunnelbackSearchService funnelbackSearchService) {
        this.funnelbackSearchService = funnelbackSearchService;
    }

    public BloomreachSearchService getBloomreachSearchService() {
        return bloomreachSearchService;
    }

    public void setBloomreachSearchService(BloomreachSearchService bloomreachSearchService) {
        this.bloomreachSearchService = bloomreachSearchService;
    }

}
