package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "publishing:featuregriditem")
@Node(jcrType = "publishing:featuregriditem")
public class Featuregriditem extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:title")
    public String getTitle() {
        return getSingleProperty("publishing:title");
    }

    @HippoEssentialsGenerated(internalName = "publishing:alt")
    public String getAlt() {
        return getSingleProperty("publishing:alt");
    }

    @HippoEssentialsGenerated(internalName = "publishing:image")
    public HippoGalleryImageSet getImage() {
        return getLinkedBean("publishing:image", HippoGalleryImageSet.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:link")
    public HippoBean getLink() {
        return getLinkedBean("publishing:link", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:externalLink")
    public String getExternalLink() {
        return getSingleProperty("publishing:externalLink");
    }

    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }
}