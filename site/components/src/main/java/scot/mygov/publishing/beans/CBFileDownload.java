package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;

@HippoEssentialsGenerated(internalName = "publishing:cb_fileDownload")
@Node(jcrType = "publishing:cb_fileDownload")
public class CBFileDownload extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:image")

    public HippoGalleryImageSet getImage() {
        return getLinkedBean("publishing:image", HippoGalleryImageSet.class);
    }
}
