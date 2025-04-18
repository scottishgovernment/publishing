package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.Parameter;

public interface FilteredResultsSideComponentInfo {

    @Parameter(name = "includePublicationTypesFilter", defaultValue = "false", required = true)
    Boolean getIncludePublicationTypesFilter();

}
