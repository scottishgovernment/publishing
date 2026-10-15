package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoCompound;

@HippoEssentialsGenerated(internalName = "publishing:featureheader")
@Node(jcrType = "publishing:featureheader")
public class Featureheader extends HippoCompound {

    @HippoEssentialsGenerated(internalName = "publishing:cta")
    public String getCta() {
        return getSingleProperty("publishing:cta");
    }

    @HippoEssentialsGenerated(internalName = "publishing:link")
    public HippoBean getLink() {
        return getLinkedBean("publishing:link", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:externalLink")
    public String getExternalLink() {
        return getSingleProperty("publishing:externalLink");
    }

    @HippoEssentialsGenerated(internalName = "publishing:backgroundColor")
    public String getBackgroundColor() {
        return getSingleProperty("publishing:backgroundColor");
    }

    @HippoEssentialsGenerated(internalName = "publishing:fullWidth")
    public Boolean getFullWidth() {
        return getSingleProperty("publishing:fullWidth");
    }

    @HippoEssentialsGenerated(internalName = "publishing:wideText")
    public Boolean getWideText() {
        return getSingleProperty("publishing:wideText");
    }

    @HippoEssentialsGenerated(internalName = "publishing:verticalAlign")
    public String getVerticalAlign() {
        return getSingleProperty("publishing:verticalAlign");
    }

    @HippoEssentialsGenerated(internalName = "publishing:imageNoMargin")
    public Boolean getImageNoMargin() {
        return getSingleProperty("publishing:imageNoMargin");
    }

    @HippoEssentialsGenerated(internalName = "publishing:imageCover")
    public Boolean getImageCover() {
        return getSingleProperty("publishing:imageCover");
    }

    @HippoEssentialsGenerated(internalName = "publishing:imageAlignMobile")
    public String getImageAlignMobile() {
        return getSingleProperty("publishing:imageAlignMobile");
    }
}
