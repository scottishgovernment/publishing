package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "publishing:video")
@Node(jcrType = "publishing:video")
public class Video extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }

    @HippoEssentialsGenerated(internalName = "publishing:alt")
    public String getAlt() {
        return getSingleProperty("publishing:alt");
    }

    @HippoEssentialsGenerated(internalName = "publishing:image")
    public HippoGalleryImageSet getImage() {
        return getLinkedBean("publishing:image", HippoGalleryImageSet.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:url")
    public String getUrl() {
        return getSingleProperty("publishing:url");
    }
}
