package scot.mygov.publishing.components.funnelback.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination {

    Page first;

    Page last;

    Page previous;

    Page next;

    List<Page> pages = new ArrayList<>();

    int currentPageIndex = 1;

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

    public Page getNext() {
        return next;
    }

    public void setNext(Page next) {
        this.next = next;
    }

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public void setCurrentPageIndex(int currentPageIndex) {
        this.currentPageIndex = currentPageIndex;
    }
}