package scot.mygov.publishing.components.eventbrite.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventbriteEvent {

    TextAndHtml name;
    TextAndHtml description;
    String url;
    String summary;
    String organisation;
    Logo logo;
    DateBlock start;
    DateBlock end;
    String dateTimeString;
    @JsonProperty("organizer_id")
    String organizerId;

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

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public DateBlock getStart() {
        return start;
    }

    public void setStart(DateBlock start) {
        this.start = start;
    }

    public DateBlock getEnd() {
        return end;
    }

    public void setEnd(DateBlock end) {
        this.end = end;
    }

    public String getDateTimeString() {
        return dateTimeString;
    }

    public void setDateTimeString(String dateTimeString) {
        this.dateTimeString = dateTimeString;
    }
}
