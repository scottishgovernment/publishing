package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.Parameter;

@FieldGroupList({
        @FieldGroup(titleKey = "Template", value = { "count" }),
        @FieldGroup(titleKey = "Appearance", value = {"neutrallinks", "removebottompadding" })
})
public interface LatestPublicationsInfo {

    @Parameter(name = "count", displayName = "Number of publications to display")
    @DropDownList(valueListProvider = LatestCountValueListProvider.class)
    String getCount();

    @Parameter(name = "neutrallinks", displayName = "Neutral link colour", defaultValue = "false")
    Boolean getNeutralLinks();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}