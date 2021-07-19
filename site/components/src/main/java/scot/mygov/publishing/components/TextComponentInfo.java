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

    @Parameter(name = "alignment", required = true, defaultValue = "left")
    @DropDownList({"left", "center", "right"})
    String getAlignment();
}
