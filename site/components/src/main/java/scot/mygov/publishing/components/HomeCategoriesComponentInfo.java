package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.*;

@FieldGroupList({
        @FieldGroup(titleKey = "Appearance", value = { "navigationType", "backgroundcolor", "removebottompadding" }),
        @FieldGroup(titleKey = "Content", value = { "category" })
})
public interface HomeCategoriesComponentInfo {

    @Parameter(name = "category", required = false)
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only",
            pickerInitialPath = "text",
            pickerSelectableNodeTypes = "publishing:category"
    )
    String getCategory();

    @Parameter(name = "navigationType", displayName = "Navigation type", required = true, defaultValue = "card")
    @DropDownList({"card", "grid", "image-card", "list"})
    String getNavigationType();

    @Parameter(name = "backgroundcolor", displayName = "Background colour", required = true, defaultValue = "secondary")
    @DropDownList({"none", "secondary"})
    String getBackgroundColor();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}
