package scot.mygov.publishing.components.eventbrite;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.Parameter;

public interface EventsComponentInfo {

    @Parameter(name = "organisationId", displayName = "Organisation ID")
    String getOrganisationId();

    @Parameter(name = "count", displayName = "Count", required = true, defaultValue = "3")
    @DropDownList({"3", "6", "9"})
    String getCount();

    @Parameter(name = "showImages", displayName = "Show images", defaultValue = "true")
    Boolean getShowImages();
}
