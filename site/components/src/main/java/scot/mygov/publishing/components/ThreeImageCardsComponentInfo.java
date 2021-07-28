package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

public interface ThreeImageCardsComponentInfo {

    String TYPE = "publishing:navigationcard";

    String INITIAL_PATH = "navigationcards";

    String CMS_PICKERS_DOCUMENTS_ONLY = "cms-pickers/documents-only";

    @Parameter(name = "document1", required = true)
    @JcrPath(
            isRelative = true,
            pickerInitialPath = INITIAL_PATH,
            pickerSelectableNodeTypes = {TYPE},
            pickerConfiguration = CMS_PICKERS_DOCUMENTS_ONLY)
    String getImage1();

    @Parameter(name = "document2")
    @JcrPath(
            isRelative = true,
            pickerInitialPath = INITIAL_PATH,
            pickerSelectableNodeTypes = {TYPE},
            pickerConfiguration = CMS_PICKERS_DOCUMENTS_ONLY)
    String getImage2();

    @Parameter(name = "document3")
    @JcrPath(
            isRelative = true,
            pickerInitialPath = INITIAL_PATH,
            pickerSelectableNodeTypes = {TYPE},
            pickerConfiguration = CMS_PICKERS_DOCUMENTS_ONLY)
    String getImage3();

    @Parameter(name = "fullwidth", displayName = "Full-width background", defaultValue = "true")
    Boolean getFullWidth();
}
