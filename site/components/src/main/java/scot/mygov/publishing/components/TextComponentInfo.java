package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

public interface TextComponentInfo {
    @Parameter(name = "document", required = true)
    @JcrPath(
            isRelative = true,
            pickerSelectableNodeTypes = {"hippo:document"}
    )
    String getDocument();

    @Parameter(name = "position", displayName = "Position", required = true, defaultValue = "left")
    @DropDownList({"left", "middle", "right"})
    String getPosition();

    @Parameter(name = "fullwidth", displayName = "Full-width background")
    Boolean getFullWidth();

    @Parameter(name = "foregroundcolor", displayName = "Text colour")
    @DropDownList(valueListProvider = ComponentForegroundColourValueListProvider.class)
    String getForegroundColor();

    @Parameter(name = "backgroundcolor", displayName = "Background colour")
    @DropDownList(valueListProvider = ComponentBackgroundColourValueListProvider.class)
    String getBackgroundColor();
}
