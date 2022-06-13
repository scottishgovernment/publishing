package scot.mygov.publishing.components.funnelback.postprocess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.components.funnelback.model.FunnelbackSearchResponse;
import scot.mygov.publishing.components.funnelback.model.Result;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Rewrite result links so that they link to pages on this environment
 */
public class ResultLinkRewriter implements PostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ResultLinkRewriter.class);

    // derived from the name of the mount - live / origin / production should be empty
    // empty (prod) or name of env (dev, int, exp etc.)
    String prefix;

    List<String> sites;

    public ResultLinkRewriter(String prefix, List<String> sites) {
        this.prefix = prefix;
        this.sites = sites;
    }

    @Override
    public void process(FunnelbackSearchResponse response) {
        for (Result result : response.getResponse().getResultPacket().getResults()) {
            String liveUrl = rewriteUrl(result.getLiveUrl());
            result.setLiveUrl(liveUrl);
        }
    }

    String rewriteUrl(String url) {
        URI uri = URI.create(url);
        String liveHost = uri.getHost();
        for (String site : sites) {
            if (liveHost.endsWith(site)) {
                return doRewrite(uri, prefix, site);
            }
        }
        return url;
    }

    String doRewrite(URI uri, String prefix, String site) {
        String newHost = prefix + "." + site;
        try {
            URI newURI = new URI(uri.getScheme(), newHost, uri.getPath(), uri.getQuery(), uri.getFragment());
            return newURI.toString();
        } catch (URISyntaxException e) {
            LOG.error("Unable to rewrite result links. url: {}, scheme: {}, host: {}, path: {}",
                    uri.toString(), uri.getScheme(), newHost, uri.getPath(), e);
            return uri.toString();
        }
    }
}
