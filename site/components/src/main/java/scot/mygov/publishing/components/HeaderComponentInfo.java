package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.Parameter;

public interface HeaderComponentInfo {
    @Parameter(name = "text", displayName = "Content", required = true, defaultValue = "header")
    String getText();

    @Parameter(name = "weight", displayName = "Header level", required = true, defaultValue = "h2")
    @DropDownList({"h2", "h3"})
    String getWeight();

    @Parameter(name = "alignment", displayName = "Alignment", required = true, defaultValue = "left")
    @DropDownList({"left", "center", "right"})
    String getAlignment();

    @Parameter(name = "fullwidth", displayName = "Full-width background")
    Boolean getFullWidth();

    @Parameter(name = "foregroundcolor", displayName = "Text colour")
    @DropDownList(valueListProvider = ComponentColourValueListProvider.class)
    String getForegroundColor();

    @Parameter(name = "backgroundcolor", displayName = "Background colour")
    @DropDownList(valueListProvider = ComponentColourValueListProvider.class)
    String getBackgroundColor();
}
