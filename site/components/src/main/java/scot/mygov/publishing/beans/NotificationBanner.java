package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import java.util.List;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "publishing:notificationbanner")
@Node(jcrType = "publishing:notificationbanner")
public class NotificationBanner extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:closeable")
    public Boolean getCloseable() {
        return getSingleProperty("publishing:closeable");
    }

    @HippoEssentialsGenerated(internalName = "publishing:bannertype")
    public String getBannertype() {
        return getSingleProperty("publishing:bannertype");
    }

    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }

    @HippoEssentialsGenerated(internalName = "publishing:excluded")
    public List<HippoBean> getExcluded() {
        return getLinkedBeans("publishing:excluded", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:id")
    public String getId() {
        return getSingleProperty("publishing:id");
    }
}
