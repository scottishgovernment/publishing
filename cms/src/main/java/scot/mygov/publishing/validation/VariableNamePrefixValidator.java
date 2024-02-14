package scot.mygov.publishing.validation;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Violation;

import javax.jcr.*;
import java.util.*;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.StringUtils.substringAfter;

/**
 * Validate resource bundles under the variables folder.
 *
 * All keys should be prefixed with a dotted string of their path, e.g.
 * variables.socialsecurity.jobstart.rate
 * this insures each key is unique and also ensures search functionality works.
 * Values should:
 * - limit length?
 * - subset of charactors, alphanumerc plus some symbols?
 */
public class VariableNamePrefixValidator extends AbstractVariablResourceBundleValidator {

    public Optional<Violation> doValidation(ValidationContext context, Node node) throws RepositoryException {
        String prefix = getPrefix(node);
        List<String> invalidKeys = getInvalidKeys(node, prefix);
        if (invalidKeys.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(violation(context, invalidKeys, prefix));
    }

    String getPrefix(Node node) throws RepositoryException {
        String prefix =  substringAfter(node.getParent().getPath(), "/content/documents/");
        return StringUtils.replace(prefix, "/", ".");
    }

    List<String> getInvalidKeys(Node node, String prefix) throws RepositoryException {
        Property keysProperty = node.getProperty("resourcebundle:keys");
        List<String> invalidKeys = new ArrayList<>();
        Value [] keys = keysProperty.getValues();
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i].getString();
            if (!startsWith(key, prefix)) {
                invalidKeys.add(key);
            }
        }
        return invalidKeys;
    }

    Violation violation(ValidationContext context, List<String> invalidKeys, String prefix) {
        Map<String, String> params = new HashMap<>();
        params.put("prefix", prefix);
        params.put("invalid-keys", invalidKeys.stream().collect(joining(", ")));
        return context.createViolation(params);
    }


}