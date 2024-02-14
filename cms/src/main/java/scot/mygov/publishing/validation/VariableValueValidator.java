package scot.mygov.publishing.validation;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Violation;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.*;

import static java.util.stream.Collectors.joining;

public class VariableValueValidator extends AbstractVariablResourceBundleValidator {

    public Optional<Violation> doValidation(ValidationContext context, Node node) throws RepositoryException {
        List<String> invalidKeys = getInvalidValues(node);
        if (invalidKeys.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(violation(context, invalidKeys));
    }

    List<String> getInvalidValues(Node node) throws RepositoryException {
        Property valuesProperty = node.getProperty("resourcebundle:messages");
        List<String> invalidValues = new ArrayList<>();
        Value[] keys = valuesProperty.getValues();
        for (int i = 0; i < keys.length; i++) {
            String value = keys[i].getString();
            String escapedValue = Jsoup.clean(value, Safelist.none());
            if (!StringUtils.equals(value, escapedValue)) {
                invalidValues.add(value);
            }
        }
        return invalidValues;
    }

    Violation violation(ValidationContext context, List<String> invalidValues) {
        Map<String, String> params = new HashMap<>();
        params.put("invalid-values", invalidValues.stream().collect(joining(", ")));
        return context.createViolation(params);
    }


}