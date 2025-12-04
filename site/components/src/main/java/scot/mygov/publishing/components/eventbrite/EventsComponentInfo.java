package scot.mygov.publishing.components.eventbrite;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.Parameter;

@FieldGroupList({
        @FieldGroup(
                titleKey = "Eventbrite",
                value = { "organisationId", "organizerId" }
        ),
        @FieldGroup(
                titleKey = "Appearance",
                value = { "title", "count", "showImages" }
        )
})
public interface EventsComponentInfo {

    @Parameter(name = "organisationId", displayName = "Organisation ID")
    String getOrganisationId();

    @Parameter(name = "organizerId", displayName = "Organizer ID")
    String getOrganizerId();

    @Parameter(name = "title", displayName = "Title", defaultValue = "Events")
    String getTitle();

    @Parameter(name = "count", displayName = "Count", required = true, defaultValue = "3")
    @DropDownList({"3", "6", "9"})
    String getCount();

    @Parameter(name = "showImages", displayName = "Show images", defaultValue = "true")
    Boolean getShowImages();
}