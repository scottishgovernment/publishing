package scot.mygov.publishing.components.funnelback;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.HstRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class SearchBuilder {

    private String query = "";

    private int page = 1;

    private boolean enableSuplimentaryQueries = true;

    private String requestUrl;

    private HstRequest request;

    SearchBuilder query(String query) {
        this.query = query;
        return this;
    }

    SearchBuilder page(int page) {
        this.page = page;
        return this;
    }

    SearchBuilder enableSuplimentaryQueries(boolean enableSuplimentaryQueries) {
        this.enableSuplimentaryQueries = enableSuplimentaryQueries;
        return this;
    }

    SearchBuilder request(HstRequest request) {
        this.request = request;
        this.requestUrl = requestUrl(request);
        return this;
    }

    private String requestUrl(HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        HttpServletRequest servletRequest = context.getServletRequest();
        return HstRequestUtils.getExternalRequestUrl(servletRequest, false);
    }

    public Search build() {
        Search search = new Search();
        search.setQuery(query);
        search.setPage(page);
        search.setRequest(request);
        search.setRequestUrl(requestUrl);
        search.setEnableSuplimentaryQueries(enableSuplimentaryQueries);
        return search;
    }
}
