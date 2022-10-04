package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import java.util.List;
import scot.mygov.publishing.beans.StepLink;

@HippoEssentialsGenerated(internalName = "publishing:Step")
@Node(jcrType = "publishing:Step")
public class Step extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:title")
    public String getTitle() {
        return getSingleProperty("publishing:title");
    }

    @HippoEssentialsGenerated(internalName = "publishing:description")
    public String getDescription() {
        return getSingleProperty("publishing:description");
    }

    @HippoEssentialsGenerated(internalName = "publishing:labeltype")
    public String getLabeltype() {
        return getSingleProperty("publishing:labeltype");
    }

    @HippoEssentialsGenerated(internalName = "publishing:links")
    public List<StepLink> getLinks() {
        return getChildBeansByName("publishing:links", StepLink.class);
    }
}
