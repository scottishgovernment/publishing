package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "publishing:imageandtext")
@Node(jcrType = "publishing:imageandtext")
public class Imageandtext extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:content")
    public String getContent() {
        return getSingleProperty("publishing:content");
    }

    @HippoEssentialsGenerated(internalName = "publishing:alt")
    public String getAlt() {
        return getSingleProperty("publishing:alt");
    }

    @HippoEssentialsGenerated(internalName = "publishing:image")
    public ImageDefault getImage() {
        return getLinkedBean("publishing:image", ImageDefault.class);
    }
}