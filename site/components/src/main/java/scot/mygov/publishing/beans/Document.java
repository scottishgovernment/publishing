package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoResourceBean;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

@HippoEssentialsGenerated(internalName = "publishing:document")
@Node(jcrType = "publishing:document")
public class Document extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:title")
    public String getTitle() {
        return getSingleProperty("publishing:title");
    }

    @HippoEssentialsGenerated(internalName = "publishing:size")
    public long getSize() {
        return getSingleProperty("publishing:size");
    }

    @HippoEssentialsGenerated(internalName = "publishing:pageCount")
    public long getPageCount() {
        return getSingleProperty("publishing:pageCount");
    }

    @HippoEssentialsGenerated(internalName = "publishing:document")
    public HippoResourceBean getDocument() {
        return getBean("publishing:document", HippoResourceBean.class);
    }
}
