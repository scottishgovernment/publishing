package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.List;
import scot.mygov.publishing.beans.Smartanswermultiplechoiceoption;

@HippoEssentialsGenerated(internalName = "publishing:smartanswermultiplechoicequestion")
@Node(jcrType = "publishing:smartanswermultiplechoicequestion")
public class Smartanswermultiplechoicequestion extends Smartanswerquestion {

    @HippoEssentialsGenerated(internalName = "publishing:options")
    public List<Smartanswermultiplechoiceoption> getOptions() {
        return getChildBeansByName("publishing:options",
                Smartanswermultiplechoiceoption.class);
    }

    public String getType() {
        return "publishing:smartanswermultiplechoicequestion";
    }
}
