package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.Parameter;

public interface HeaderComponentInfo {

    @Parameter(name = "text", required = true, defaultValue = "header")
    String getText();

    @Parameter(name = "weight", required = true, defaultValue = "h2")
    @DropDownList({"h2", "h3"})
    String getWeight();

    @Parameter(name = "alignment", required = true, defaultValue = "left")
    @DropDownList({"left", "center", "right"})
    String getAlignment();
}