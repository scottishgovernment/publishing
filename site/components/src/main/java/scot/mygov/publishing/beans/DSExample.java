package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;

@HippoEssentialsGenerated(internalName = "publishing:dsexample")
@Node(jcrType = "publishing:dsexample")
public class DSExample extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:illustration")
    public HippoGalleryImageSet getIllustration() {
        return getLinkedBean("publishing:illustration", HippoGalleryImageSet.class);
    }
}
