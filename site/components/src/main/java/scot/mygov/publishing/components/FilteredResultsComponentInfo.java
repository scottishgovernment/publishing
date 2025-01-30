package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.Parameter;
import org.onehippo.cms7.essentials.components.info.EssentialsListComponentInfo;

public interface FilteredResultsComponentInfo extends EssentialsListComponentInfo {

    @Parameter(name = "defaultSort")
    String getDefaultSort();
}