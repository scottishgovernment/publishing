package scot.mygov.publishing.validation;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;
import org.onehippo.repository.util.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.Optional;

/**
 * Created by z418868 on 04/10/2021.
 */
public class DefaultNextSmartAnswerPageValidator implements Validator<Node> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultNextSmartAnswerPageValidator.class);

    HippoUtils hippoUtils = new HippoUtils();

    public Optional<Violation> validate(ValidationContext context, Node value) {
        // this field is required if any of the answers on this page do not specify a next page.


        // find all of the answers ...
         /// do any of them hae no next page speciofied?
        try {
            return doValidate(context, value);
        } catch (RepositoryException e) {
            LOG.error("Failed to validate the organisation tag {} from the ValueList", value, e);
            return Optional.of(context.createViolation("Failed to validate the organisation tag"));
        }
    }

    Optional<Violation> doValidate(ValidationContext context, Node value) throws RepositoryException {
        Node doc = context.getDocumentNode();
        NodeIterator options = doc.getNodes("publishing:options");
        Node optionWithNoNextPage = hippoUtils.findFirst(options, this::hasNoNextPage);

        if (optionWithNoNextPage == null) {
            // all of the options have a next page so the default is not required
            return Optional.empty();
        }

        // at least one option has no next page specified, so this is a required field
        if (!mirrorHasDocbase(value)) {
            return Optional.of(context.createViolation("next-answer-required"));
        }

        return Optional.empty();
    }

    boolean hasNoNextPage(Node option) throws RepositoryException {
        Node nextPage = option.getNode("publishing:nextPage");
        return !mirrorHasDocbase(nextPage);
    }

    boolean mirrorHasDocbase(Node mirror) throws RepositoryException {
        String docbase = mirror.getProperty("hippo:docbase").getString();
        return !StringUtils.equals(JcrConstants.ROOT_NODE_ID, docbase);
    }
}