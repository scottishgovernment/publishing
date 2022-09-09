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

    Safelist messageHtmlSafelist =
            new Safelist()
                .addTags("a", "dd", "dl", "dt", "em", "li", "ol", "p", "pre", "q", "small", "strong", "sub", "sup", "u", "ul")
                .addAttributes("a", "href")
                .addAttributes("q", "cite")
                .addProtocols("a", "href", "http", "https", "mailto")
                .addEnforcedAttribute("a", "rel", "nofollow");

    private CacheLoader<String, String> messageHtmllLoader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return Jsoup.clean(key, messageHtmlSafelist);
        }
    };

    private LoadingCache<String, String> messageHtmlCache = CacheBuilder.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(CACHE_EXPIRY_DURATION)
            .build(messageHtmllLoader);

    private CacheLoader<String, String> stripHtmllLoader = new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return Jsoup.clean(key, Safelist.none());
        }
    };

    private LoadingCache<String, String> stripAllHtmlCache = CacheBuilder.newBuilder()
            .maximumSize(MAX_CACHE_SIZE)
            .expireAfterAccess(CACHE_EXPIRY_DURATION)
            .build(stripHtmllLoader);

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
        exhibit.setMessageHtml(cleanMessageHtmlStr(exhibit.getMessageHtml()));
        exhibit.setDescriptionHtml(stripAllHtml(exhibit.getDescriptionHtml()));
        exhibit.setTitleHtml(stripAllHtml(exhibit.getTitleHtml()));
        return exhibit;
    }

    String cleanMessageHtmlStr(String str) {
        try {
            return StringUtils.isBlank(str) ? "" : messageHtmlCache.get(str);
        } catch (ExecutionException e) {
            LOG.error("Failed to get sanitised html from messageHtmlCache for \"{}\"", str, e);
            return "";
        }
    }

    String stripAllHtml(String str) {
        try {
            return StringUtils.isBlank(str) ? "" : stripAllHtmlCache.get(str);
        } catch (ExecutionException e) {
            LOG.error("Failed to get sanitised html from stripAllHtmlCache for \"{}\"", str, e);
            return "";
        }
    }

}
