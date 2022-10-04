package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "publishing:StepLink")
@Node(jcrType = "publishing:StepLink")
public class StepLink extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:linkdescription")
    public String getLinkdescription() {
        return getSingleProperty("publishing:linkdescription");
    }

    @HippoEssentialsGenerated(internalName = "publishing:link")
    public HippoBean getLink() {
        return getLinkedBean("publishing:link", HippoBean.class);
    }
}
