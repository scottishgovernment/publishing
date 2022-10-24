package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "publishing:siteverification")
@Node(jcrType = "publishing:siteverification")
public class Siteverification extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:code")
    public String getCode() {
        return getSingleProperty("publishing:code");
    }

    @HippoEssentialsGenerated(internalName = "publishing:type")
    public String getType() {
        return getSingleProperty("publishing:type");
    }
}
