package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "publishing:organisationlist")
@Node(jcrType = "publishing:organisationlist")
public class Organisationlist extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:title")
    public String getTitle() {
        return getSingleProperty("publishing:title");
    }

    @HippoEssentialsGenerated(internalName = "publishing:summary")
    public String getSummary() {
        return getSingleProperty("publishing:summary");
    }

    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }

    @HippoEssentialsGenerated(internalName = "publishing:metaDescription")
    public String getMetaDescription() {
        return getSingleProperty("publishing:metaDescription");
    }

    @HippoEssentialsGenerated(internalName = "publishing:seoTitle")
    public String getSeoTitle() {
        return getSingleProperty("publishing:seoTitle");
    }

    @HippoEssentialsGenerated(internalName = "publishing:html")
    public HippoHtml getHtml() {
        return getHippoHtml("publishing:html");
    }
}
