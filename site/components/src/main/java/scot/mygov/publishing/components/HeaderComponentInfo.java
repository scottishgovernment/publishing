package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.Parameter;

@FieldGroupList({
    @FieldGroup(titleKey = "Appearance", value = { "position", "backgroundcolor", "removebottompadding" }),
    @FieldGroup(titleKey = "Content", value = { "text", "weight" })
})

public interface HeaderComponentInfo {
    @Parameter(name = "text", displayName = "Content", required = true, defaultValue = "Header")
    String getText();

    @Parameter(name = "weight", displayName = "Header level", required = true, defaultValue = "h2")
    @DropDownList({"h2", "h3"})
    String getWeight();

    @Parameter(name = "position", displayName = "Position", required = true, defaultValue = "left")
    @DropDownList({"left", "center", "right"})
    String getPosition();

    @Parameter(name = "backgroundcolor", displayName = "Background colour")
    @DropDownList({"Secondary", "Tertiary", "Theme"})
    String getBackgroundColor();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}
