package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "publishing:home")
@Node(jcrType = "publishing:home")
public class Home extends Base {
    @HippoEssentialsGenerated(internalName = "publishing:heroImage")
    public HippoGalleryImageSet getHeroImage() {
        return getLinkedBean("publishing:heroImage", HippoGalleryImageSet.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:prologue")
    public HippoHtml getPrologue() {
        return getHippoHtml("publishing:prologue");
    }

    @HippoEssentialsGenerated(internalName = "publishing:epilogue")
    public HippoHtml getEpilogue() {
        return getHippoHtml("publishing:epilogue");
    }
}
