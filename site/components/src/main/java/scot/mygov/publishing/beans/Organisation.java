package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "publishing:organisation")
@Node(jcrType = "publishing:organisation")
public class Organisation extends Base {

    @HippoEssentialsGenerated(internalName = "publishing:serviceprovider")
    public String getServiceProvider() {
        return getSingleProperty("publishing:serviceprovider");
    }

    @HippoEssentialsGenerated(internalName = "publishing:sector")
    public String getSector() {
        return getSingleProperty("publishing:sector");
    }

    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }

}
