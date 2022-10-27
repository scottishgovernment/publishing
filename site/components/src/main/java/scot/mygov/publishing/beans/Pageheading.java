package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
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

    @HippoEssentialsGenerated(internalName = "publishing:cta")
    public String getCta() {
        return getSingleProperty("publishing:cta");
    }

    @HippoEssentialsGenerated(internalName = "publishing:link")
    public HippoBean getLink() {
        return getLinkedBean("publishing:link", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:externalLink")
    public String getExternalLink() {
        return getSingleProperty("publishing:externalLink");
    }

    @HippoEssentialsGenerated(internalName = "publishing:aside")
    public HippoHtml getAside() {
        return getHippoHtml("publishing:aside");
    }

    @HippoEssentialsGenerated(internalName = "publishing:asideIcon")
    public HippoGalleryImageSet getAsideIcon() {
        return getLinkedBean("publishing:asideIcon", HippoGalleryImageSet.class);
    }
}
