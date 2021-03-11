package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;

@HippoEssentialsGenerated(internalName = "publishing:featureditem")
@Node(jcrType = "publishing:featureditem")
public class Featureditem extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:link")
    public HippoBean getLink() {
        return getLinkedBean("publishing:link", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:image")
    public HippoGalleryImageSet getImage() {
        return getLinkedBean("publishing:image", HippoGalleryImageSet.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:summary")
    public String getSummary() {
        return getSingleProperty("publishing:summary");
    }
}
