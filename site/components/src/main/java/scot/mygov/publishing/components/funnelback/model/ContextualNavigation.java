package scot.mygov.publishing.components.funnelback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContextualNavigation {

    private String searchTerm;

    private List<ContextualNavigationCategory> categories = new ArrayList<>();

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public List<ContextualNavigationCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<ContextualNavigationCategory> categories) {
        this.categories = categories;
    }
}
