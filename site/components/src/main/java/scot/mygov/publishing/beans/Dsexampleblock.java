package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "publishing:dsexampleblock")
@Node(jcrType = "publishing:dsexampleblock")
public class Dsexampleblock extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:type")
    public String getType() {
        return getSingleProperty("publishing:type");
    }

    @HippoEssentialsGenerated(internalName = "publishing:showcode")
    public Boolean getShowcode() {
        return getSingleProperty("publishing:showcode");
    }

    @HippoEssentialsGenerated(internalName = "publishing:showdemo")
    public Boolean getShowdemo() {
        return getSingleProperty("publishing:showdemo");
    }

    @HippoEssentialsGenerated(internalName = "publishing:htmlexpanded")
    public Boolean getHtmlexpanded() {
        return getSingleProperty("publishing:htmlexpanded");
    }

    @HippoEssentialsGenerated(internalName = "publishing:note")
    public String getNote() {
        return getSingleProperty("publishing:note");
    }

    @HippoEssentialsGenerated(internalName = "publishing:minheight")
    public Long getMinheight() {
        return getSingleProperty("publishing:minheight");
    }

    @HippoEssentialsGenerated(internalName = "publishing:example")
    public HippoBean getExample() {
        return getLinkedBean("publishing:example", HippoBean.class);
    }
}
