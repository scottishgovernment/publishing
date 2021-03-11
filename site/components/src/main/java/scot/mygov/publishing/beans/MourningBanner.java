package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "publishing:mourningbanner")
@Node(jcrType = "publishing:mourningbanner")
public class MourningBanner extends BaseDocument {

    @HippoEssentialsGenerated(internalName = "publishing:html")
    public String getHtml() {
        return getSingleProperty("publishing:html");
    }

}
