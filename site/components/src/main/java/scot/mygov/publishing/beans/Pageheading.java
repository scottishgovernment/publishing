package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "publishing:pageheading")
@Node(jcrType = "publishing:pageheading")
public class Pageheading extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:title")
    public String getTitle() {
        return getSingleProperty("publishing:title");
    }

    @HippoEssentialsGenerated(internalName = "publishing:alt")
    public String getAlt() {
        return getSingleProperty("publishing:alt");
    }

    @HippoEssentialsGenerated(internalName = "publishing:description")
    public HippoHtml getDescription() {
        return getHippoHtml("publishing:description");
    }

    @HippoEssentialsGenerated(internalName = "publishing:image")
    public HippoGalleryImageSet getImage() {
        return getLinkedBean("publishing:image", HippoGalleryImageSet.class);
    }
}
