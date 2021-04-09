package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "publishing:mourningbanner")
@Node(jcrType = "publishing:mourningbanner")
public class MourningBanner extends BaseDocument {

    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }

    @HippoEssentialsGenerated(internalName = "publishing:authorNotes")
    public HippoHtml getAuthorNotes() {
        return getHippoHtml("publishing:authorNotes");
    }
}
