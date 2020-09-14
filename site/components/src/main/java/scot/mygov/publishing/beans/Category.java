package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "publishing:category")
@Node(jcrType = "publishing:category")
public class Category extends Base {
    @HippoEssentialsGenerated(internalName = "publishing:navigationType")
    public String getNavigationType() {
        return getSingleProperty("publishing:navigationType");
    }

    @HippoEssentialsGenerated(internalName = "publishing:sequenceable")
    public Boolean getSequenceable() {
        return getSingleProperty("publishing:sequenceable");
    }

    @HippoEssentialsGenerated(internalName = "publishing:prologue")
    public HippoHtml getPrologue() {
        return getHippoHtml("publishing:prologue");
    }

    @HippoEssentialsGenerated(internalName = "publishing:epilogue")
    public HippoHtml getEpilogue() {
        return getHippoHtml("publishing:epilogue");
    }

    @HippoEssentialsGenerated(internalName = "publishing:heroImage")
    public HippoGalleryImageSet getHeroImage() {
        return getLinkedBean("publishing:heroImage", HippoGalleryImageSet.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:cardImage")
    public ImageCard getCardImage() {
        return getLinkedBean("publishing:cardImage", ImageCard.class);
    }

    @HippoEssentialsGenerated(internalName = "hippotaxonomy:keys")
    public String[] getKeys() {
        return getMultipleProperty("hippotaxonomy:keys");
    }
}
