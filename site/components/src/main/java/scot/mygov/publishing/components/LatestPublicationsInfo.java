package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.Parameter;

@FieldGroupList({
        @FieldGroup(titleKey = "Template", value = { "count" }),
        @FieldGroup(titleKey = "Appearance", value = {"removebottompadding" })
})
public interface LatestPublicationsInfo {

    @Parameter(name = "count", displayName = "Number of publications to display", defaultValue = "3")
    @DropDownList(valueListProvider = LatestCountValueListProvider.class)
    String getCount();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}