package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;

@HippoEssentialsGenerated(internalName = "publishing:linkwithlabel")
@Node(jcrType = "publishing:linkwithlabel")
public class Linkwithlabel extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:url")
    public String getUrl() {
        return getSingleProperty("publishing:url");
    }

    @HippoEssentialsGenerated(internalName = "publishing:label")
    public String getLabel() {
        return getSingleProperty("publishing:label");
    }
}
