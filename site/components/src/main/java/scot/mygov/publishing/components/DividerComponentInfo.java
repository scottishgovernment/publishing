package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.Parameter;

public interface DividerComponentInfo {
    @Parameter(name = "fullwidth", displayName = "Full-width divider", defaultValue = "true")
    Boolean getFullWidth();
}
