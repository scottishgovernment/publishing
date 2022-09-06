package scot.mygov.publishing.components.funnelback.postprocess;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.funnelback.model.Curator;
import scot.gov.publishing.hippo.funnelback.model.Exhibit;
import scot.gov.publishing.hippo.funnelback.model.FunnelbackSearchResponse;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.partitioningBy;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class CuratorPostProcessor implements PostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(CuratorPostProcessor.class);

    private static final int MAX_CACHE_SIZE = 200;

    private static final Duration CACHE_EXPIRY_DURATION = Duration.ofHours(1);

    private CacheLoader<String, String> loader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return Jsoup.clean(key, Safelist.basic());
        }
    };

    private LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(CACHE_EXPIRY_DURATION)
            .build(loader);

    @Override
    public void process(FunnelbackSearchResponse response) {
        Curator curator = response.getResponse().getCurator();
        List<Exhibit> exhibits = curator.getExhibits();
        Map<Boolean, List<Exhibit>> partitioned
                = exhibits.stream().map(this::clean).collect(partitioningBy(this::simple));
        curator.setSimpleHtmlExhibits(partitioned.get(true));
        curator.setAdvertExhibits(partitioned.get(false));
    }

    boolean simple(Exhibit exhibit) {
        return isNotBlank(exhibit.getMessageHtml());
    }

    Exhibit clean(Exhibit exhibit) {
        exhibit.setDescriptionHtml(cleanStr(exhibit.getDescriptionHtml()));
        exhibit.setMessageHtml(cleanStr(exhibit.getMessageHtml()));
        exhibit.setTitleHtml(cleanStr(exhibit.getTitleHtml()));
        return exhibit;
    }

    String cleanStr(String str) {
        try {
            return StringUtils.isBlank(str) ? "" : cache.get(str);
        } catch (ExecutionException e) {
            LOG.error("Failed to get sanitised html from cache for \"{}\"", str, e);
            return "";
        }
    }

}
