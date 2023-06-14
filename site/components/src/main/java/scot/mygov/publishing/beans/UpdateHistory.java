package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;

import java.util.Calendar;

@HippoEssentialsGenerated(internalName = "publishing:UpdateHistory")
@Node(jcrType = "publishing:UpdateHistory")
public class UpdateHistory extends HippoCompound {

    @HippoEssentialsGenerated(internalName = "publishing:updateText")
    public String getUpdateText() {
        return getSingleProperty("publishing:updateText");
    }

    @HippoEssentialsGenerated(internalName = "publishing:updateTextLong")
    public HippoHtml getUpdateTextLong() {
        return getHippoHtml("publishing:updateTextLong");
    }

    @HippoEssentialsGenerated(internalName = "publishing:lastUpdated")
    public Calendar getLastUpdated() {
        return getSingleProperty("publishing:lastUpdated");
    }
}
