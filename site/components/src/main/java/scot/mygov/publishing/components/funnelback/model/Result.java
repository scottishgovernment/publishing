package scot.mygov.publishing.components.funnelback.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    HippoBean bean;

    int rank;

    int score;

    String title;

    String liveUrl;

    String bloomreachPath;

    String summary;

    String allSummaryText;

    String cacheUrl;

    long date;

    List<String> image = new ArrayList<>();

    List<String> dcTitle = new ArrayList<>();

    ListMetadata listMetadata = new ListMetadata();

    String displayUrl;

    String clickTrackingUrl;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    public String getBloomreachPath() {
        return bloomreachPath;
    }

    public void setBloomreachPath(String bloomreachPath) {
        this.bloomreachPath = bloomreachPath;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAllSummaryText() {
        return allSummaryText;
    }

    public void setAllSummaryText(String allSummaryText) {
        this.allSummaryText = allSummaryText;
    }

    public String getCacheUrl() {
        return cacheUrl;
    }

    public void setCacheUrl(String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<String> getImage() {
        return image;
    }

    public void setImage(List<String> image) {
        this.image = image;
    }

    public List<String> getDcTitle() {
        return dcTitle;
    }

    public void setDcTitle(List<String> dcTitle) {
        this.dcTitle = dcTitle;
    }

    public ListMetadata getListMetadata() {
        return listMetadata;
    }

    public void setListMetadata(ListMetadata listMetadata) {
        this.listMetadata = listMetadata;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getClickTrackingUrl() {
        return clickTrackingUrl;
    }

    public void setClickTrackingUrl(String clickTrackingUrl) {
        this.clickTrackingUrl = clickTrackingUrl;
    }

    public HippoBean getBean() {
        return bean;
    }

    public void setBean(HippoBean bean) {
        this.bean = bean;
    }
}