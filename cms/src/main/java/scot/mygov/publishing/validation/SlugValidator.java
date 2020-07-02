package scot.mygov.publishing.validation;

import org.onehippo.cms.services.validation.api.Violation;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.Collections;
import java.util.Optional;

/**
 * Validate that a slug:
 * - is lower case
 * - only contains letters, numbers and hyphens
 * - is slugClashes within the site it is part of
 */
public class SlugValidator implements Validator<String> {

    private static final Logger LOG = LoggerFactory.getLogger(SlugValidator.class);

    UrlValidationUtils urlValidationUtils = new UrlValidationUtils();

    public Optional<Violation> validate(ValidationContext context, String slug) {

        try {
            Violation violation = doValidate(context, slug);

            return violation == null
                    ? Optional.empty()
                    : Optional.of(violation);
        } catch (RepositoryException e) {
            LOG.error("failed to validate slug {} for node {}", slug, context.getJcrName(), e);
            return Optional.of(context.createViolation("failed to validate slug"));
        }
    }

    Violation doValidate(ValidationContext context, String slug) throws RepositoryException {

        if (urlValidationUtils.containsInvalidCharacters(slug)) {
            return context.createViolation("slug-invalid-chars");
        }

        String clash = urlValidationUtils.slugClashes(slug, context.getDocumentNode());
        if (clash != null) {
            return violationForClash(context, "slug-clash", clash);
        }

        clash = urlValidationUtils.slugClashesWithAlias(slug, context.getDocumentNode());
        if (clash != null) {
            return violationForClash(context, "slug-alias-clash", clash);
        }

        clash = urlValidationUtils.slugClashesWithCategory(slug, context.getDocumentNode());
        if (clash != null) {
            return violationForClash(context, "slug-category-clash", clash);
        }

        return null;
    }

    Violation violationForClash(ValidationContext context, String key, String clash) {
        return context.createViolation(key, Collections.singletonMap("clash", clash));
    }
}