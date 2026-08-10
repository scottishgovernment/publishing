package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

public interface PageNavigationComponentInfo {

    @Parameter(name = "topLevelNavItem", displayName = "Top level navigation item")
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only"
    )
    String getTopLevelNavItem();
}
