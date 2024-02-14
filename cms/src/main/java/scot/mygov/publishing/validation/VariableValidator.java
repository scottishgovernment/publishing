package scot.mygov.publishing.validation;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scot.gov.variables.VariablesHelper;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substringBetween;

/**
 * Node level validator for variable interpolation.
 *
 * Recursively checks all string properties to ensure any variables exist in one of the relevant resource bundles.
 */
public class VariableValidator implements Validator<Node> {

    private static final Logger LOG = LoggerFactory.getLogger(VariableValidator.class);

    private ResourceBundle resourceBundle;

    @Override
    public Optional<Violation> validate(ValidationContext context, Node node) {
        try {
            resourceBundle = VariablesHelper.getVariablesResourceBundle(node.getSession());
            SortedSet<String> invalidKeys = new TreeSet<>(checkNodeProps(node));
            if (invalidKeys.isEmpty()) {
                return Optional.empty();
            }
            String keys = invalidKeys.stream().collect(joining(", "));
            Violation violation = context.createViolation(Collections.singletonMap("keys", keys));
            return Optional.of(violation);
        } catch (RepositoryException e){
            LOG.error("An exception occurred while trying to validate a document for existence of resource bundle keys.", e);
        } catch (RuntimeException e) {
            LOG.error("An runtime exception occurred while trying to validate a document for existence of resource bundle keys.", e);
        }
        return Optional.empty();
    }

    private List<String> checkNodeProps(Node node) throws RepositoryException {
        List<String> invalidKeys = new ArrayList<>();
        PropertyIterator propertyIterator = node.getProperties();
        while (propertyIterator.hasNext()) {
            Property property = propertyIterator.nextProperty();
            validateProperty(property, invalidKeys);
        }

        NodeIterator nodeIterator = node.getNodes();
        while(nodeIterator.hasNext()){
            Node currentNode = nodeIterator.nextNode();
            invalidKeys.addAll(checkNodeProps(currentNode));
        }
        return invalidKeys;
    }

    void validateProperty(Property property, List<String> invalidKeys) throws RepositoryException {
        if (PropertyType.STRING != property.getType()) {
            return;
        }

        if (!property.isMultiple()) {
            validateValue(property.getString(), invalidKeys);
            return;
        }

        Value[] multiValue = property.getValues();
        for (Value value : multiValue) {
            validateValue(value.getString(), invalidKeys);
        }
    }

    void validateValue(String value, List<String> invalidKeys) {
        List<String> keys = extractKeys(value);
        keys.stream().filter(this::isInvalidKey).forEach(key -> invalidKeys.add(key));
    }

    private boolean isInvalidKey(String key){
        return isNotBlank(key) && !resourceBundle.containsKey(key);
    }

    private List<String> extractKeys(String prop) {
        Matcher matcher = Pattern.compile("\\[\\[(.*?)\\]\\]").matcher(prop);
        List<String> keys = new ArrayList<String>();
        while (matcher.find()) {
            String key = substringBetween(matcher.group(), "[[", "]]");
            keys.add(key);
        }
        return keys;
    }
}
