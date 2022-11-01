package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;
import org.onehippo.cms7.essentials.components.info.EssentialsDocumentComponentInfo;

@FieldGroupList({
    @FieldGroup(titleKey = "Appearance", value = {"backgroundcolor", "fullwidth", "foregroundcolor", "neutrallinks"}),
    @FieldGroup(titleKey = "Text formatting", value = {"widetext", "verticalalign", "lightheader" }),
    @FieldGroup(titleKey = "Image formatting ", value = {"imagenomargin", "imagealigndesktop", "imagealignmobile" }),
    @FieldGroup(titleKey = "Aside", value = {"asidebackgroundcolor", "hideasideicon"}),
    @FieldGroup(titleKey = "Content", value = { "document" })
})

public interface PageheadingComponentInfo extends EssentialsDocumentComponentInfo {

    @Parameter(name = "document", required = true)
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only",
            pickerInitialPath = "pageheadings",
            pickerSelectableNodeTypes = "publishing:pageheading"
    )
    String getDocument();

    @Parameter(name = "fullwidth", displayName = "Full-width background", defaultValue = "true")
    Boolean getFullWidth();

    @Parameter(name = "foregroundcolor", displayName = "Heading colour (on white background)")
    @DropDownList(valueListProvider = ComponentForegroundColourValueListProvider.class)
    String getForegroundColor();

    @Parameter(name = "backgroundcolor", displayName = "Background colour")
    @DropDownList(valueListProvider = ComponentBackgroundColourValueListProvider.class)
    String getBackgroundColor();

    @Parameter(name = "neutrallinks", displayName = "Neutral link colour", defaultValue = "false")
    Boolean getNeutralLinks();

    @Parameter(name = "verticalalign", displayName = "Vertical align text", required = true, defaultValue = "top")
    @DropDownList({"top", "middle"})
    String getVerticalAlign();

    @Parameter(name = "widetext", displayName = "Increase width of text", defaultValue = "false")
    Boolean getWideText();

    @Parameter(name = "lightheader", displayName = "Lighter heading style", defaultValue = "false")
    Boolean getLightHeader();

    @Parameter(name = "imagenomargin", displayName = "Remove vertical margins (image desktop)", defaultValue = "false")
    Boolean getImageNoMargin();

    @Parameter(name = "imagealigndesktop", displayName = "Vertical alignment (image desktop)", defaultValue = "middle")
    @DropDownList({"top", "middle", "cover"})
    String getImageAlignDesktop();

    @Parameter(name = "imagealignmobile", displayName = "Horizontal alignment (image mobile)", defaultValue = "left")
    @DropDownList({"left", "right", "center", "hidden"})
    String getImageAlignMobile();

    @Parameter(name = "asidebackgroundcolor", displayName = "Background colour")
    @DropDownList(valueListProvider = ComponentBackgroundColourValueListProvider.class)
    String getAsideBackgroundColor();

    @Parameter(name = "hideasideicon", displayName = "Hide icon", defaultValue = "false")
    Boolean getHideAsideIcon();
}