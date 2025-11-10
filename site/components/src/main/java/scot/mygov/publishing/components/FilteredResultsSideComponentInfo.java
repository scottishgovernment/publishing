package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.Parameter;

public interface FilteredResultsSideComponentInfo {

    @Parameter(name = "includePublicationTypesFilter", defaultValue = "false", required = true)
    Boolean getIncludePublicationTypesFilter();

    @Parameter(name = "includeNews", defaultValue = "true", required = true)
    Boolean getIncludeNews();

    @Parameter(name = "includeLanguages", defaultValue = "false", required = true)
    Boolean getIncludeLanguages();

    @Parameter(name = "includeAccessibilityFeatures", defaultValue = "false", required = true)
    Boolean getIncludeAccessibilityFeatures();
}
