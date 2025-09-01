package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.*;

@FieldGroupList({
        @FieldGroup(titleKey = "Appearance", value = { "navigationType", "backgroundcolor", "removebottompadding" }),
        @FieldGroup(titleKey = "Content", value = { "category" })
})
public interface HomeCategoriesComponentInfo {

    @Parameter(name = "category", required = true)
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

    @Parameter(name = "backgroundcolor", displayName = "Background colour")
    @DropDownList({"secondary", "tertiary", "theme", "theme reversed"})
    String getBackgroundColor();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}
