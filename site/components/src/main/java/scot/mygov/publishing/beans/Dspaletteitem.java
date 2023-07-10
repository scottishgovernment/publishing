package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;

@HippoEssentialsGenerated(internalName = "publishing:dspaletteitem")
@Node(jcrType = "publishing:dspaletteitem")
public class Dspaletteitem extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:colourname")
    public String getColourname() {
        return getSingleProperty("publishing:colourname");
    }

    @HippoEssentialsGenerated(internalName = "publishing:varname")
    public String getVarname() {
        return getSingleProperty("publishing:varname");
    }

    @HippoEssentialsGenerated(internalName = "publishing:code")
    public String getCode() {
        return getSingleProperty("publishing:code");
    }
}
