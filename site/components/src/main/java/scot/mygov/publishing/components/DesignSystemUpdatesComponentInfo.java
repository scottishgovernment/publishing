package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.Parameter;

public interface DesignSystemUpdatesComponentInfo {

    @Parameter(name = "limit", displayName = "Update limit", defaultValue = "3")
    Integer getLimit();

}