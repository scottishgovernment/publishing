package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "publishing:googleverification")
@Node(jcrType = "publishing:googleverification")
public class Googleverification extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:code")
    public String getCode() {
        return getSingleProperty("publishing:code");
    }
}
