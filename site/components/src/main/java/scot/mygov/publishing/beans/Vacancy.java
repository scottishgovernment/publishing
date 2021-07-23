package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.Calendar;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@HippoEssentialsGenerated(internalName = "publishing:vacancy")
@Node(jcrType = "publishing:vacancy")
public class Vacancy extends Base {
    @HippoEssentialsGenerated(internalName = "publishing:location")
    public String getLocation() {
        return getSingleProperty("publishing:location");
    }

    @HippoEssentialsGenerated(internalName = "publishing:reference")
    public String getReference() {
        return getSingleProperty("publishing:reference");
    }

    @HippoEssentialsGenerated(internalName = "publishing:salaryband")
    public String getSalaryband() {
        return getSingleProperty("publishing:salaryband");
    }

    @HippoEssentialsGenerated(internalName = "publishing:link")
    public String getLink() {
        return getSingleProperty("publishing:link");
    }

    @HippoEssentialsGenerated(internalName = "publishing:closingDate")
    public Calendar getClosingDate() {
        return getSingleProperty("publishing:closingDate");
    }

    @HippoEssentialsGenerated(internalName = "publishing:showFeedback")
    public Boolean getShowFeedback() {
        return getSingleProperty("publishing:showFeedback");
    }

    @HippoEssentialsGenerated(internalName = "publishing:content")
    public HippoHtml getContent() {
        return getHippoHtml("publishing:content");
    }
}
