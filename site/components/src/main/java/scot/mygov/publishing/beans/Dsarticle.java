package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

@HippoEssentialsGenerated(internalName = "publishing:dsarticle")
@Node(jcrType = "publishing:dsarticle")
public class Dsarticle extends Base {
    @HippoEssentialsGenerated(internalName = "publishing:experimental")
    public Boolean getExperimental() {
        return getSingleProperty("publishing:experimental");
    }

    @HippoEssentialsGenerated(internalName = "publishing:deprecated")
    public Boolean getDeprecated() {
        return getSingleProperty("publishing:deprecated");
    }

    @HippoEssentialsGenerated(internalName = "hippotaxonomy:keys")
    public String[] getKeys() {
        return getMultipleProperty("hippotaxonomy:keys");
    }

    @HippoEssentialsGenerated(internalName = "publishing:showFeedback")
    public Boolean getShowFeedback() {
        return getSingleProperty("publishing:showFeedback");
    }

    @HippoEssentialsGenerated(internalName = "publishing:deprecatednote")
    public HippoHtml getDeprecatednote() {
        return getHippoHtml("publishing:deprecatednote");
    }

    @HippoEssentialsGenerated(internalName = "publishing:UpdateHistory")
    public List<UpdateHistory> getUpdateHistory() {
        return getChildBeansByName("publishing:UpdateHistory",
                UpdateHistory.class);
    }

    @Override
    public Calendar getLastUpdatedDate() {
        return getUpdateHistory().stream()
                .map(UpdateHistory::getLastUpdated)
                .max(Comparator.naturalOrder())
                .orElseGet(() -> getSingleProperty("hippostdpubwf:publicationDate"));
    }
}
