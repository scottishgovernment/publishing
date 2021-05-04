package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "publishing:facebookverification")
@Node(jcrType = "publishing:facebookverification")
public class Facebookverification extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:code")
    public String getCode() {
        return getSingleProperty("publishing:code");
    }
}
