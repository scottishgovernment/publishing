package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import java.util.Calendar;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSet;
import java.util.List;
import scot.mygov.publishing.beans.DescribedLink;
import scot.mygov.publishing.beans.ImageDefault;
import scot.mygov.publishing.beans.Featureditem;

@HippoEssentialsGenerated(internalName = "publishing:organisation")
@Node(jcrType = "publishing:organisation")
public class Organisation extends Base {
    @HippoEssentialsGenerated(internalName = "publishing:serviceprovider")
    public String getServiceProvider() {
        return getSingleProperty("publishing:serviceprovider");
    }

    @HippoEssentialsGenerated(internalName = "publishing:sector")
    public String getSector() {
        return getSingleProperty("publishing:sector");
    }

    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }

    @HippoEssentialsGenerated(internalName = "publishing:featuredrolename")
    public String getFeaturedrolename() {
        return getSingleProperty("publishing:featuredrolename");
    }

    @HippoEssentialsGenerated(internalName = "publishing:featuredroledescription")
    public String getFeaturedroledescription() {
        return getSingleProperty("publishing:featuredroledescription");
    }

    @HippoEssentialsGenerated(internalName = "publishing:fax")
    public String getFax() {
        return getSingleProperty("publishing:fax");
    }

    @HippoEssentialsGenerated(internalName = "publishing:email")
    public String getEmail() {
        return getSingleProperty("publishing:email");
    }

    @HippoEssentialsGenerated(internalName = "publishing:address")
    public String getAddress() {
        return getSingleProperty("publishing:address");
    }

    @HippoEssentialsGenerated(internalName = "publishing:twitter")
    public String getTwitter() {
        return getSingleProperty("publishing:twitter");
    }

    @HippoEssentialsGenerated(internalName = "publishing:flickr")
    public String getFlickr() {
        return getSingleProperty("publishing:flickr");
    }

    @HippoEssentialsGenerated(internalName = "publishing:website")
    public String getWebsite() {
        return getSingleProperty("publishing:website");
    }

    @HippoEssentialsGenerated(internalName = "publishing:facebook")
    public String getFacebook() {
        return getSingleProperty("publishing:facebook");
    }

    @HippoEssentialsGenerated(internalName = "publishing:youtube")
    public String getYoutube() {
        return getSingleProperty("publishing:youtube");
    }

    @HippoEssentialsGenerated(internalName = "publishing:blog")
    public String getBlog() {
        return getSingleProperty("publishing:blog");
    }

    @HippoEssentialsGenerated(internalName = "publishing:featuredrole")
    public String getFeaturedrole() {
        return getSingleProperty("publishing:featuredrole");
    }

    @HippoEssentialsGenerated(internalName = "publishing:phone")
    public String getPhone() {
        return getSingleProperty("publishing:phone");
    }

    @HippoEssentialsGenerated(internalName = "publishing:featureditemstitle")
    public String getFeatureditemstitle() {
        return getSingleProperty("publishing:featureditemstitle");
    }

    @HippoEssentialsGenerated(internalName = "publishing:featuredservicestitle")
    public String getFeaturedservicestitle() {
        return getSingleProperty("publishing:featuredservicestitle");
    }

    @HippoEssentialsGenerated(internalName = "publishing:organisationtitle")
    public String getOrganisationtitle() {
        return getSingleProperty("publishing:organisationtitle");
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

    @HippoEssentialsGenerated(internalName = "publishing:logo")
    public HippoGalleryImageSet getLogo() {
        return getLinkedBean("publishing:logo", HippoGalleryImageSet.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:featuredservices")
    public List<DescribedLink> getFeaturedservices() {
        return getChildBeansByName("publishing:featuredservices",
                DescribedLink.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:notices")
    public HippoHtml getNotices() {
        return getHippoHtml("publishing:notices");
    }

    @HippoEssentialsGenerated(internalName = "publishing:featuredroleimage")
    public ImageDefault getFeaturedroleimage() {
        return getLinkedBean("publishing:featuredroleimage", ImageDefault.class);
    }

    @HippoEssentialsGenerated(internalName = "publishing:organisationstructure")
    public HippoHtml getOrganisationstructure() {
        return getHippoHtml("publishing:organisationstructure");
    }

    @HippoEssentialsGenerated(internalName = "publishing:featureditem")
    public List<Featureditem> getFeatureditem() {
        return getChildBeansByName("publishing:featureditem",
                Featureditem.class);
    }
}
