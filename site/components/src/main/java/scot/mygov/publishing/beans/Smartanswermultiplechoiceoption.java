package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "publishing:smartanswermultiplechoiceoption")
@Node(jcrType = "publishing:smartanswermultiplechoiceoption")
public class Smartanswermultiplechoiceoption extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:value")
    public String getValue() {
        return getSingleProperty("publishing:value");
    }

    @HippoEssentialsGenerated(internalName = "publishing:label")
    public String getLabel() {
        return getSingleProperty("publishing:label");
    }

    @HippoEssentialsGenerated(internalName = "publishing:nextPage")
    public HippoBean getNextPage() {
        return getLinkedBean("publishing:nextPage", HippoBean.class);
    }

}
