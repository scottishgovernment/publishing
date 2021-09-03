package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;
import org.onehippo.cms7.essentials.components.info.EssentialsDocumentComponentInfo;

public interface SEOComponentInfo extends EssentialsDocumentComponentInfo {

    @Parameter(name = "showTitle", defaultValue = "true")
    boolean getShowTitle();

    @Parameter(name = "document", required = true)
    @JcrPath(
            isRelative = true,
            pickerConfiguration = "cms-pickers/documents-only",
            pickerInitialPath = "seo",
            pickerSelectableNodeTypes = "publishing:seo"
    )
    String getDocument();
}