package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.Parameter;

public interface HeaderComponentInfo {
    @Parameter(name = "text", displayName = "Content", required = true, defaultValue = "header")
    String getText();

    @Parameter(name = "weight", displayName = "Header level", required = true, defaultValue = "h2")
    @DropDownList({"h2", "h3"})
    String getWeight();

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
