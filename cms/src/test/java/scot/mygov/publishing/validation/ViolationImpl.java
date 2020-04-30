package scot.mygov.publishing.validation;

import org.onehippo.cms.services.validation.api.Violation;

import java.util.Map;

/**
 * Created by z418868 on 27/04/2020.
 */
public class ViolationImpl implements Violation {

    private String subKey;

    private Map<String, String> parameters;

    @Override
    public String getMessage() {
        return null;
    }

    public String getSubkey() {
        return subKey;
    }

    public void setSubkey(String subKey) {
        this.subKey = subKey;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
