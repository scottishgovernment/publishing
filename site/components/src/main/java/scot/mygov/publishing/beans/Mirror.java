package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "publishing:mirror")
@Node(jcrType = "publishing:mirror")
public class Mirror extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:document")
    public HippoBean getDocument() {
        return getLinkedBean("publishing:document", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:pinned")
    public Boolean getPinned() {
        return getSingleProperty("publishing:pinned");
    }
}
