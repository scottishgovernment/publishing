package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

import static scot.mygov.publishing.beans.PublicationPage.getPagesFolder;

@HippoEssentialsGenerated(internalName = "publishing:ManualDocuments")
@Node(jcrType = "publishing:ManualDocuments")
public class ManualDocuments extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:title")
    public String getTitle() {
        return getSingleProperty("publishing:title");
    }

    @HippoEssentialsGenerated(internalName = "publishing:seoTitle")
    public String getSeoTitle() {
        return getSingleProperty("publishing:seoTitle");
    }

    @HippoEssentialsGenerated(internalName = "publishing:summary")
    public String getSummary() {
        return getSingleProperty("publishing:summary");
    }

    @HippoEssentialsGenerated(internalName = "publishing:metaDescription")
    public String getMetaDescription() {
        return getSingleProperty("publishing:metaDescription");
    }

    @HippoEssentialsGenerated(internalName = "hippotaxonomy:keys")
    public String[] getKeys() {
        return getMultipleProperty("hippotaxonomy:keys");
    }
    public HippoBean getPartOfBean() {
        HippoFolderBean pagesFolder = getPagesFolder(this);
        return pagesFolder == null ? null : pagesFolder.getParentBean().getBean("index");
    }

}
