package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import scot.mygov.publishing.beans.ImageDefault;

@HippoEssentialsGenerated(internalName = "publishing:seo")
@Node(jcrType = "publishing:seo")
public class Seo extends Base {
    @HippoEssentialsGenerated(internalName = "publishing:showFeedback")
    public Boolean getShowFeedback() {
        return getSingleProperty("publishing:showFeedback");
    }
}
