package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "publishing:smartanswerresult")
@Node(jcrType = "publishing:smartanswerresult")
public class Smartanswerresult extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:answer")
    public HippoHtml getAnswer() {
        return getHippoHtml("publishing:answer");
    }
}
