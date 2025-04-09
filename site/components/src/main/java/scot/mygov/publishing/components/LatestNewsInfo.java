package scot.mygov.publishing.components;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;

import org.hippoecm.hst.core.parameters.*;

@FieldGroupList({
    @FieldGroup(titleKey = "Template", value = { "count" }),
    @FieldGroup(titleKey = "Appearance", value = {"allowImages", "neutrallinks", "removebottompadding" })
})

public interface LatestNewsInfo {

    @Parameter(name = "count", displayName = "Number of articles to display", defaultValue = "3")
    @DropDownList(valueListProvider = LatestCountValueListProvider.class)
    String getCount();

    @Parameter(name = "allowImages", displayName = "Allow images if all articles have an image", defaultValue = "true")
    Boolean getAllowImages();

    @Parameter(name = "neutrallinks", displayName = "Neutral link colour", defaultValue = "false")
    Boolean getNeutralLinks();

    @Parameter(name = "removebottompadding", displayName = "Remove bottom padding", defaultValue = "false")
    Boolean getRemoveBottomPadding();
}