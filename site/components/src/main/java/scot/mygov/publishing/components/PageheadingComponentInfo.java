package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;
import org.onehippo.cms7.essentials.components.info.EssentialsDocumentComponentInfo;

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

    @Parameter(name = "foregroundcolor", displayName = "Text colour (on white background)")
    @DropDownList(valueListProvider = ComponentForegroundColourValueListProvider.class)
    String getForegroundColor();

    @Parameter(name = "backgroundcolor", displayName = "Background colour")
    @DropDownList(valueListProvider = ComponentBackgroundColourValueListProvider.class)
    String getBackgroundColor();
}