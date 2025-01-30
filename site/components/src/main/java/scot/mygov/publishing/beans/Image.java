package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;

@HippoEssentialsGenerated(internalName = "publishing:Image")
@Node(jcrType = "publishing:Image")
public class Image extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:alt")
    public String getAlt() {
        return getSingleProperty("publishing:alt");
    }

    @HippoEssentialsGenerated(internalName = "publishing:caption")
    public String getCaption() {
        return getSingleProperty("publishing:caption");
    }

    @HippoEssentialsGenerated(internalName = "publishing:credit")
    public String getCredit() {
        return getSingleProperty("publishing:credit");
    }

    @HippoEssentialsGenerated(internalName = "publishing:image")
    public ColumnImage getImage() {
        return getLinkedBean("publishing:image", ColumnImage.class);
    }
}
