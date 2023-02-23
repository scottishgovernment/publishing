package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.List;

@HippoEssentialsGenerated(internalName = "publishing:smartanswersingleselectquestion")
@Node(jcrType = "publishing:smartanswersingleselectquestion")
public class Smartanswersingleselectquestion extends Smartanswerquestion {

    @HippoEssentialsGenerated(internalName = "publishing:options")
    public List<Smartanswersingleselectoption> getOptions() {
        return getChildBeansByName("publishing:options",
                Smartanswersingleselectoption.class);
    }

    public String getType() {
        return "publishing:smartanswersingleselectquestion";
    }
}
