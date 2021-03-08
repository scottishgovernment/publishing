package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "publishing:sector")
@Node(jcrType = "publishing:sector")
public class Sector extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:description")
    public String getDescription() {
        return getSingleProperty("publishing:description");
    }

    @HippoEssentialsGenerated(internalName = "publishing:sector")
    public String getSector() {
        return getSingleProperty("publishing:sector");
    }

    @HippoEssentialsGenerated(internalName = "publishing:title")
    public String getTitle() {
        return getSingleProperty("publishing:title");
    }
}
