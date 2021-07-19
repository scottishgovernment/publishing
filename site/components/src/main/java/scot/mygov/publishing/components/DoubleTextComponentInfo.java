package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;


public interface DoubleTextComponentInfo {
    @Parameter(name = "document1", required = true)
    @JcrPath(
            isRelative = true,
            pickerSelectableNodeTypes = {"hippo:document"}
    )
    String getDocument1();

    @Parameter(name = "alignment1", required = true, defaultValue = "left")
    @DropDownList({"left", "center", "right"})
    String getAlignment1();

    @Parameter(name = "document2", required = true)
    @JcrPath(
            isRelative = true,
            pickerSelectableNodeTypes = {"hippo:document"}
    )
    String getDocument2();

    @Parameter(name = "alignment2", required = true, defaultValue = "left")
    @DropDownList({"left", "center", "right"})
    String getAlignment2();
}
