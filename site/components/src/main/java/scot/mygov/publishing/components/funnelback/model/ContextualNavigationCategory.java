package scot.mygov.publishing.components.funnelback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContextualNavigationCategory {

    private String name;

    private List<ContextualNavigationCluster> clusters = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ContextualNavigationCluster> getClusters() {
        return clusters;
    }

    public void setClusters(List<ContextualNavigationCluster> clusters) {
        this.clusters = clusters;
    }
}
