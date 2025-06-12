package scot.mygov.publishing.components.eventbright;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

    TextAndHtml name;
    TextAndHtml description;
    String url;
    String summary;
    Logo logo;

    public TextAndHtml getName() {
        return name;
    }

    public void setName(TextAndHtml name) {
        this.name = name;
    }

    public TextAndHtml getDescription() {
        return description;
    }

    public void setDescription(TextAndHtml description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Logo getLogo() {
        return logo;
    }

    public void setLogo(Logo logo) {
        this.logo = logo;
    }
}
