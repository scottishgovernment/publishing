package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

import static scot.mygov.publishing.components.ThreeImageCardsComponent.CMS_PICKERS_DOCUMENTS_ONLY;
import static scot.mygov.publishing.components.ThreeImageCardsComponent.INITIAL_PATH;
import static scot.mygov.publishing.components.ThreeImageCardsComponent.TYPE;

@FieldGroupList({
    @FieldGroup(titleKey = "Appearance", value = { "greycards", "fullwidth", "showimages", "small", "neutrallinks" }),
    @FieldGroup(titleKey = "Content", value = { "document1", "document2", "document3" })
})

public interface ThreeImageCardsComponentInfo {

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

    @Parameter(name = "showimages", displayName = "Show images", defaultValue = "true")
    Boolean getShowImages();

    @Parameter(name = "small", displayName = "Use small variant on mobile", defaultValue = "true")
    Boolean getSmallVariant();

    @Parameter(name = "neutrallinks", displayName = "Neutral link colour", defaultValue = "false")
    Boolean getNeutralLinks();

    @Parameter(name = "greycards", displayName = "Grey cards on white background", defaultValue = "false")
    Boolean getGreyCards();
}
