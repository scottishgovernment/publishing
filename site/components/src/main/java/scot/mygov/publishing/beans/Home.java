package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import java.util.Calendar;
import java.util.List;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "publishing:home")
@Node(jcrType = "publishing:home")
public class Home extends Base {
    @HippoEssentialsGenerated(internalName = "publishing:navigationType")
    public String getNavigationType() {
        return getSingleProperty("publishing:navigationType");
    }

    @HippoEssentialsGenerated(internalName = "publishing:prologue")
    public HippoHtml getPrologue() {
        return getHippoHtml("publishing:prologue");
    }

    @HippoEssentialsGenerated(internalName = "publishing:epilogue")
    public HippoHtml getEpilogue() {
        return getHippoHtml("publishing:epilogue");
    }

    @HippoEssentialsGenerated(internalName = "publishing:heroImage")
    public ColumnImage getHeroImage() {
        return getLinkedBean("publishing:heroImage", ColumnImage.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:cardImage")
    public ImageCard getCardImage() {
        return getLinkedBean("publishing:cardImage", ImageCard.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:showSummaries")
    public Boolean getShowSummaries() {
        return getSingleProperty("publishing:showSummaries");
    }

    @HippoEssentialsGenerated(internalName = "publishing:urlAliases")
    public String[] getUrlAliases() {
        return getMultipleProperty("publishing:urlAliases");
    }

    @HippoEssentialsGenerated(internalName = "publishing:sensitive")
    public Boolean getSensitive() {
        return getSingleProperty("publishing:sensitive");
    }

    @HippoEssentialsGenerated(internalName = "publishing:audience")
    public String getAudience() {
        return getSingleProperty("publishing:audience");
    }

    @HippoEssentialsGenerated(internalName = "publishing:serviceproviders")
    public String[] getServiceproviders() {
        return getMultipleProperty("publishing:serviceproviders");
    }

    @HippoEssentialsGenerated(internalName = "publishing:lifeEvents")
    public String[] getLifeEvents() {
        return getMultipleProperty("publishing:lifeEvents");
    }

    @HippoEssentialsGenerated(internalName = "publishing:userneed")
    public String getUserneed() {
        return getSingleProperty("publishing:userneed");
    }

    @HippoEssentialsGenerated(internalName = "publishing:reviewDate")
    public Calendar getReviewDate() {
        return getSingleProperty("publishing:reviewDate");
    }

    @HippoEssentialsGenerated(internalName = "publishing:lastUpdatedDate")
    public Calendar getLastUpdatedDate() {
        return getSingleProperty("publishing:lastUpdatedDate");
    }

    @HippoEssentialsGenerated(internalName = "publishing:popularpanelitem")
    public List<DescribedLink> getPopularpanelitem() {
        return getChildBeansByName("publishing:popularpanelitem",
                DescribedLink.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:contentOwner")
    public HippoBean getContentOwner() {
        return getLinkedBean("publishing:contentOwner", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:factCheckers")
    public List<HippoBean> getFactCheckers() {
        return getLinkedBeans("publishing:factCheckers", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:showFeedback")
    public Boolean getShowFeedback() {
        return getSingleProperty("publishing:showFeedback");
    }
}
