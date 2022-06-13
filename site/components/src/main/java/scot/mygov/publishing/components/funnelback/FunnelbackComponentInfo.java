package scot.mygov.publishing.components.funnelback;

import org.hippoecm.hst.core.parameters.Parameter;
import org.onehippo.cms7.essentials.components.info.EssentialsPageable;
import org.onehippo.cms7.essentials.components.info.EssentialsSortable;


public interface FunnelbackComponentInfo extends EssentialsSortable, EssentialsPageable {
    @Parameter(name = "collection")
    String getCollection();

    @Parameter(name = "profile", defaultValue = "_default_live")
    String getProfile();

}
