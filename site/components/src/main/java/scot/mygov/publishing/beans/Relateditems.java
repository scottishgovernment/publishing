package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import java.util.List;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "publishing:relateditems")
@Node(jcrType = "publishing:relateditems")
public class Relateditems extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:title")
    public String getTitle() {
        return getSingleProperty("publishing:title");
    }

    @HippoEssentialsGenerated(internalName = "publishing:relatedItem")
    public List<HippoBean> getRelatedItem() {
        return getLinkedBeans("publishing:relatedItem", HippoBean.class);
    }
}
