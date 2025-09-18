package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "publishing:PublicationPage")
@Node(jcrType = "publishing:PublicationPage")
public class PublicationPage extends BaseDocument {
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

    public HippoBean getPartOfBean() {
        HippoFolderBean pagesFolder = getPagesFolder(this);
        return pagesFolder == null ? null : pagesFolder.getParentBean().getBean("index");
    }

    static HippoFolderBean getPagesFolder(HippoBean current) {

        while (true) {
            HippoBean parent = current.getParentBean();
            if (parent == null) {
                return null;
            }

            if (parent instanceof HippoFolderBean && "pages".equals(parent.getName())) {
                return (HippoFolderBean) parent;
            }

            current = parent;
        }
    }
}
