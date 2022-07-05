package scot.mygov.publishing.components.funnelback;

import org.hippoecm.hst.core.component.HstRequest;

/**
 * Encapsulate the params needed to perform a search.
 */
public class Search {
    private String query;

    private int page;

    private boolean enableSuplimentaryQueries;

    private String requestUrl;

    private HstRequest request;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isEnableSuplimentaryQueries() {
        return enableSuplimentaryQueries;
    }

    public void setEnableSuplimentaryQueries(boolean enableSuplimentaryQueries) {
        this.enableSuplimentaryQueries = enableSuplimentaryQueries;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public HstRequest getRequest() {
        return request;
    }

    public void setRequest(HstRequest request) {
        this.request = request;
    }
}
