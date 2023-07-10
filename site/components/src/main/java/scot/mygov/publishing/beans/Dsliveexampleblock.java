package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "publishing:dsliveexampleblock")
@Node(jcrType = "publishing:dsliveexampleblock")
public class Dsliveexampleblock extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }
}
