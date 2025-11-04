package scot.mygov.publishing.components.eventbrite.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventbriteResults {

    List<EventbriteEvent> events = new ArrayList<>();

    Pagination pagination = new Pagination();

    public List<EventbriteEvent> getEvents() {
        return events;
    }

    public void setEvents(List<EventbriteEvent> events) {
        this.events = events;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
