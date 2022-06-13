package scot.mygov.publishing.components.funnelback.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ListMetadata {

    List<String> titleSeries;

    List<String> titleSeriesLink;

    List<String> c;

    List<String> t;

    public List<String> getTitleSeries() {
        return titleSeries;
    }

    public void setTitleSeries(List<String> titleSeries) {
        this.titleSeries = titleSeries;
    }

    public List<String> getTitleSeriesLink() {
        return titleSeriesLink;
    }

    public void setTitleSeriesLink(List<String> titleSeriesLink) {
        this.titleSeriesLink = titleSeriesLink;
    }

    public List<String> getC() {
        return c;
    }

    public void setC(List<String> c) {
        this.c = c;
    }

    public List<String> getT() {
        return t;
    }

    public void setT(List<String> t) {
        this.t = t;
    }
}