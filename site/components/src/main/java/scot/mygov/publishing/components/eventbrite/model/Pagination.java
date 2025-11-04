package scot.mygov.publishing.components.eventbrite.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination {

    @JsonProperty("object_count")
    int objectCount;

    public int getObjectCount() {
        return objectCount;
    }

    public void setObjectCount(int objectCount) {
        this.objectCount = objectCount;
    }
}
