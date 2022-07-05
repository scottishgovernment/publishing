package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "publishing:searchsettings")
@Node(jcrType = "publishing:searchsettings")
public class Searchsettings extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:searchtype")
    public String getSearchtype() {
        return getSingleProperty("publishing:searchtype");
    }

    @HippoEssentialsGenerated(internalName = "publishing:timeoutMillis")
    public Long getTimeoutMillis() {
        return getSingleProperty("publishing:timeoutMillis");
    }

    @HippoEssentialsGenerated(internalName = "publishing:enabled")
    public Boolean getEnabled() {
        return getSingleProperty("publishing:enabled");
    }
}
