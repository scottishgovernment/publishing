package scot.mygov.publishing.validation;

import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.contentbean.ValueListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

/**
 * Used to ensure fields match values within Organisation Tag ValueList.
 */
public class OrganisationTagValidator implements Validator<String> {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisationTagValidator.class);

    HippoUtils hippoUtils;

    public OrganisationTagValidator() {
        this.hippoUtils = new HippoUtils();
    }

    public OrganisationTagValidator(HippoUtils hippoUtils) {
        this.hippoUtils = hippoUtils;
    }

    public Optional<Violation> validate(ValidationContext context, String value) {
        try {
            Session session = context.getDocumentNode().getSession();
            Violation violation = doValidate(context, value, session);

            return violation == null
                    ? Optional.empty()
                    : Optional.of(violation);

        } catch (RepositoryException e) {
            LOG.error("Failed to validate the organisation tag {} from the ValueList", value);
            return Optional.of(context.createViolation("Failed to validate the organisation tag"));
        }

    }

    Violation doValidate(ValidationContext context, String value, Session session) throws RepositoryException {
        List<String> organisationTags;
        try {
            organisationTags = getValidOrganisationTags(session);
            if (!organisationTags.contains(value)) {
                return context.createViolation("invalid-organisation-tag", Collections.singletonMap("tag", value));
            }
        } catch (RepositoryException e) {
            LOG.error("Unable to retrieve organisation ValueList", e);
        }
        return null;
    }

    protected List<String> getValidOrganisationTags(Session session) throws RepositoryException {
        ValueList organisationTags =
                hippoUtils.getValueList(session, "/content/documents/publishing/valuelists/organisationtags");
        List<ValueListItem> items = organisationTags.getItems();
        Set<String> validValues = new HashSet<>();
        for (ValueListItem item : items) {
            validValues.add(item.getLabel());
        }
        return new ArrayList<>(validValues);
    }
}