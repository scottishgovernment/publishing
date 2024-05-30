package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.Parameter;
import org.onehippo.cms7.essentials.components.info.EssentialsDocumentComponentInfo;

@FieldGroupList({
    @FieldGroup(titleKey = "Appearance", value = {"backgroundcolor", "fullwidth", "foregroundcolor", "neutrallinks", "removebottompadding" }),
    @FieldGroup(titleKey = "Content", value = { "document" })
})

public interface ImageAndTextComponentInfo extends EssentialsDocumentComponentInfo {

    @Parameter(name = "document", required = true)
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only",
            pickerInitialPath = "images",
            pickerSelectableNodeTypes = "publishing:imageandtext"

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

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}
