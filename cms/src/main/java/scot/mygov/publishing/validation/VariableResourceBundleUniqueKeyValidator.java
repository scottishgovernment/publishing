package scot.mygov.publishing.validation;

import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Violation;
import javax.jcr.*;
import javax.jcr.query.Query;
import java.util.*;

import static scot.gov.variables.VariablesHelper.VARIABLE_QUERY;

/**
 * All resource bundles under the variable path needs ot have a unique key
 */
public class VariableResourceBundleUniqueKeyValidator extends AbstractVariablResourceBundleValidator {

    public Optional<Violation> doValidation(ValidationContext context, Node node) throws RepositoryException {
        String id = node.getProperty("resourcebundle:id").getString();
        NodeIterator iterator = resourceBundleNodes(node.getSession());
        while (iterator.hasNext()) {
            Node resultNode = iterator.nextNode();
            if (!resultNode.getParent().isSame(node.getParent())
                    && id.equals(resultNode.getProperty("resourcebundle:id").getString())) {
                return Optional.of(context.createViolation());
            }
        }
        return Optional.empty();
    }

    NodeIterator resourceBundleNodes(Session session) throws RepositoryException {
        return session.getWorkspace()
                .getQueryManager()
                .createQuery(VARIABLE_QUERY, Query.XPATH)
                .execute()
                .getNodes();
    }
}