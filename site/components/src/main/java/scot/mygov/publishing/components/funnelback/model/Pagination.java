package scot.mygov.publishing.components.funnelback.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination {

    Page first;

    Page last;

    Page previous;

    List<Page> pages;

    public Page getFirst() {
        return first;
    }

    public void setFirst(Page first) {
        this.first = first;
    }

    public Page getLast() {
        return last;
    }

    public void setLast(Page last) {
        this.last = last;
    }

    public Page getPrevious() {
        return previous;
    }

    public void setPrevious(Page previous) {
        this.previous = previous;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }
}
