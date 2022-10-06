package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import java.util.List;
import scot.mygov.publishing.beans.Step;

@HippoEssentialsGenerated(internalName = "publishing:StepByStepGuide")
@Node(jcrType = "publishing:StepByStepGuide")
public class StepByStepGuide extends Base {

    @HippoEssentialsGenerated(internalName = "publishing:slug")
    public String getSlug() {
        return getSingleProperty("publishing:slug");
    }

    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }

    @HippoEssentialsGenerated(internalName = "publishing:steps")
    public List<Step> getSteps() {
        return getChildBeansByName("publishing:steps", Step.class);
    }
}
