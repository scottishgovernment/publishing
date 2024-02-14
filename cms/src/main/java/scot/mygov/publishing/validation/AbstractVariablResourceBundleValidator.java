package scot.mygov.publishing.validation;

import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static scot.gov.variables.VariablesHelper.VARIABLES_PATH;

public abstract class AbstractVariablResourceBundleValidator implements Validator<Node> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractVariablResourceBundleValidator.class);

    @Override
    public Optional<Violation> validate(ValidationContext context, Node node) {

        try {
            if (startsWith(node.getPath(), VARIABLES_PATH)) {
                return doValidation(context, node);
            }
        } catch (RepositoryException e) {
            LOG.error("An exception occurred while trying to validate variable keys", e);
        }
        return Optional.empty();
    }

    public abstract Optional<Violation> doValidation(ValidationContext context, Node node) throws RepositoryException;
}