package scot.mygov.publishing.sitemap;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.hippoecm.hst.core.request.SiteMapItemHandlerConfiguration;
import org.hippoecm.hst.core.sitemapitemhandler.HstSiteMapItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TrailingSlashRedirect implements HstSiteMapItemHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TrailingSlashRedirect.class);

    @Override
    public void init(ServletContext servletContext, SiteMapItemHandlerConfiguration handlerConfig) {
        // Nothing to initialise
    }

    @Override
    public ResolvedSiteMapItem process(
            ResolvedSiteMapItem resolvedSiteMapItem,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (!request.getRequestURL().toString().endsWith("/")) {
            return resolvedSiteMapItem;
        }

        // do not redirect request if they have an extension.
        if (hasExtension(request.getPathTranslated())) {
            return resolvedSiteMapItem;
        }


        String redirect = redirectTarget(request);
        LOG.debug("Sending trailing slash redirect to {}", redirect);
        response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        response.setHeader("Location", redirect);

        // Stop further processing on this request
        return null;
    }

    boolean hasExtension(String url) {
        String lastSegment = StringUtils.substringAfterLast(url, "/");
        return StringUtils.contains(lastSegment, ".");
    }

    private String redirectTarget(HttpServletRequest request) {
        String urlWithoutSlash = StringUtils.removeEnd(request.getRequestURL().toString(), "/");
        StringBuilder url = new StringBuilder(urlWithoutSlash);
        if (request.getQueryString() != null) {
            url.append('?').append(request.getQueryString());
        }
        return url.toString();
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }
}
