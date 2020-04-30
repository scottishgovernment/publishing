package scot.mygov.publishing.validation;

import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Violation;

import javax.jcr.Node;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by z418868 on 27/04/2020.
 */
public class ValidationContextImpl implements ValidationContext {

    @Override
    public String getJcrName() {
        return "";
    }

    @Override
    public String getJcrType() {
        return "";
    }

    @Override
    public String getType() {
        return "";
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public TimeZone getTimeZone() {
        return null;
    }

    @Override
    public Node getParentNode() {
        return null;
    }

    @Override
    public Node getDocumentNode() {
        return null;
    }

    @Override
    public Violation createViolation() {
        return createViolation((Map<String, String>) null);
    }

    @Override
    public Violation createViolation(final Map<String, String> parameters) {
        ViolationImpl violation = new ViolationImpl();
        violation.setParameters(parameters);
        return violation;
    }

    @Override
    public Violation createViolation(final String subKey) {
        ViolationImpl violation = new ViolationImpl();
        violation.setSubkey(subKey);
        return violation;
    }

    @Override
    public Violation createViolation(final String subKey, final Map<String, String> parameters) {
        ViolationImpl violation = new ViolationImpl();
        violation.setSubkey(subKey);
        violation.setParameters(parameters);
        return violation;
    }
}
