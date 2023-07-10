package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoCompound;
import java.util.List;
import scot.mygov.publishing.beans.Dspaletteitem;

@HippoEssentialsGenerated(internalName = "publishing:dspaletteblock")
@Node(jcrType = "publishing:dspaletteblock")
public class Dspaletteblock extends HippoCompound {
    @HippoEssentialsGenerated(internalName = "publishing:title")
    public String getTitle() {
        return getSingleProperty("publishing:title");
    }

    @HippoEssentialsGenerated(internalName = "publishing:headinglevel")
    public String getHeadinglevel() {
        return getSingleProperty("publishing:headinglevel");
    }

    @HippoEssentialsGenerated(internalName = "publishing:paletteitems")
    public List<Dspaletteitem> getPaletteitems() {
        return getChildBeansByName("publishing:paletteitems",
                Dspaletteitem.class);
    }
}
