package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import java.util.Calendar;
import java.util.List;
import scot.mygov.publishing.beans.Relateditems;
import org.hippoecm.hst.content.beans.standard.HippoBean;

@HippoEssentialsGenerated(internalName = "publishing:article")
@Node(jcrType = "publishing:article")
public class Article extends Base {
    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }

    @HippoEssentialsGenerated(internalName = "publishing:slug")
    public String getSlug() {
        return getSingleProperty("publishing:slug");
    }

    @HippoEssentialsGenerated(internalName = "publishing:audience")
    public String getAudience() {
        return getSingleProperty("publishing:audience");
    }

    @HippoEssentialsGenerated(internalName = "publishing:serviceproviders")
    public String[] getServiceproviders() {
        return getMultipleProperty("publishing:serviceproviders");
    }

    @HippoEssentialsGenerated(internalName = "hippotaxonomy:keys")
    public String[] getKeys() {
        return getMultipleProperty("hippotaxonomy:keys");
    }

    @HippoEssentialsGenerated(internalName = "publishing:urlAliases")
    public String[] getUrlAliases() {
        return getMultipleProperty("publishing:urlAliases");
    }

    @HippoEssentialsGenerated(internalName = "publishing:sensitive")
    public Boolean getSensitive() {
        return getSingleProperty("publishing:sensitive");
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

    @HippoEssentialsGenerated(internalName = "publishing:relateditems")
    public List<Relateditems> getRelateditems() {
        return getChildBeansByName("publishing:relateditems",
                Relateditems.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:logo")
    public HippoGalleryImageSet getLogo() {
        return getLinkedBean("publishing:logo", HippoGalleryImageSet.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:contentOwner")
    public HippoBean getContentOwner() {
        return getLinkedBean("publishing:contentOwner", HippoBean.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:factCheckers")
    public List<HippoBean> getFactCheckers() {
        return getLinkedBeans("publishing:factCheckers", HippoBean.class);
    }
}
