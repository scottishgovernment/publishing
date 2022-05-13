package scot.mygov.publishing.components.funnelback.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultPacket {

    String query = "";

    String queryAsProcessed = "";

    String queryRaw = "";

    String queryCleaned = "";

    ResultsSummary resultsSummary = new ResultsSummary();

    List<Result> results = new ArrayList<>();

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryAsProcessed() {
        return queryAsProcessed;
    }

    public void setQueryAsProcessed(String queryAsProcessed) {
        this.queryAsProcessed = queryAsProcessed;
    }

    public String getQueryRaw() {
        return queryRaw;
    }

    public void setQueryRaw(String queryRaw) {
        this.queryRaw = queryRaw;
    }

    public String getQueryCleaned() {
        return queryCleaned;
    }

    public void setQueryCleaned(String queryCleaned) {
        this.queryCleaned = queryCleaned;
    }

    public ResultsSummary getResultsSummary() {
        return resultsSummary;
    }

    public void setResultsSummary(ResultsSummary resultsSummary) {
        this.resultsSummary = resultsSummary;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }
}
