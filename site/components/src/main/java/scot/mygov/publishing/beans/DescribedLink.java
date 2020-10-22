package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import java.util.List;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "publishing:describedlink")
@Node(jcrType = "publishing:describedlink")
public class DescribedLink extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:title")
    public String getTitle() {
        return getSingleProperty("publishing:title");
    }

    @HippoEssentialsGenerated(internalName = "publishing:description")
    public String getDescription() {
        return getSingleProperty("publishing:description");
    }

    @HippoEssentialsGenerated(internalName = "publishing:link")
    public HippoBean getLink() {
        return getLinkedBean("publishing:link", HippoBean.class);
    }
}
