package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "publishing:cookiepreferences")
@Node(jcrType = "publishing:cookiepreferences")
public class Cookiepreferences extends Base {
    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }
}
