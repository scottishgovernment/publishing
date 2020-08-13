package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;

import java.util.List;

@HippoEssentialsGenerated(internalName = "publishing:documentcoverpage")
@Node(jcrType = "publishing:documentcoverpage")
public class DocumentCoverPage extends Base {

    @HippoEssentialsGenerated(internalName = "publishing:documents")
    public List<Document> getDocuments() {
        return getChildBeansByName("publishing:documents", Document.class);
    }

}
