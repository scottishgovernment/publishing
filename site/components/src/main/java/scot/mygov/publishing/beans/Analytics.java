package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "publishing:analytics")
@Node(jcrType = "publishing:analytics")
public class Analytics extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:notes")
    public String getNotes() {
        return getSingleProperty("publishing:notes");
    }

    @HippoEssentialsGenerated(internalName = "publishing:env")
    public String getEnv() {
        return getSingleProperty("publishing:env");
    }

    @HippoEssentialsGenerated(internalName = "publishing:auth")
    public String getAuth() {
        return getSingleProperty("publishing:auth");
    }

    @HippoEssentialsGenerated(internalName = "publishing:containerid")
    public String getContainerid() {
        return getSingleProperty("publishing:containerid");
    }
}
