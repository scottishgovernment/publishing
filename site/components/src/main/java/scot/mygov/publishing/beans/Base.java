package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "publishing:base")
@Node(jcrType = "publishing:base")
public class Base extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:summary")
    public String getSummary() {
        return getSingleProperty("publishing:summary");
    }

    @HippoEssentialsGenerated(internalName = "publishing:title")
    public String getTitle() {
        return getSingleProperty("publishing:title");
    }

    @HippoEssentialsGenerated(internalName = "publishing:metaDescription")
    public String getMetaDescription() {
        return getSingleProperty("publishing:metaDescription");
    }

    @HippoEssentialsGenerated(internalName = "publishing:seoTitle")
    public String getSeoTitle() {
        return getSingleProperty("publishing:seoTitle");
    }

    @HippoEssentialsGenerated(internalName = "publishing:authorNotes")
    public String getAuthorNotes() {
        return getSingleProperty("publishing:authorNotes");
    }

    @HippoEssentialsGenerated(internalName = "publishing:reportingtags")
    public String[] getReportingtags() {
        return getMultipleProperty("publishing:reportingtags");
    }

    @HippoEssentialsGenerated(internalName = "hippostd:tags")
    public String[] getTags() {
        return getMultipleProperty("hippostd:tags");
    }
}
