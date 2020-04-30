package scot.mygov.publishing.validation;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Validate url aliases.  Similar to slug validation, but has to do some additional checks:
 * - clash with another alias
 * - clash with a slug?
 * - clash with a category url
 * - must have a leading slash
 * - must not have a trailing slash
 */
public class UrlAliasValidator implements Validator<String> {

    private static final Logger LOG = LoggerFactory.getLogger(SlugValidator.class);

    UrlValidationUtils urlValidationUtils = new UrlValidationUtils();

    public Optional<Violation> validate(ValidationContext context, String slug) {

        try {
            Violation violation = doValidate(context, slug);

            return violation == null
                    ? Optional.empty()
                    : Optional.of(violation);
        } catch (RepositoryException e) {
            LOG.error("falied to validate slug {} for node {}", slug, context.getJcrName(), e);
            return Optional.of(context.createViolation("failed to validate slug"));
        }
    }

    Violation doValidate(ValidationContext context, String alias) throws RepositoryException {
        if (StringUtils.isEmpty(alias)) {
            return context.createViolation("alias-empty");
        }

        if (!StringUtils.startsWith(alias, "/")) {
            return violationForAlias(context, "alias-leading-slash", alias);
        }

        if (StringUtils.endsWith(alias, "/")) {
            return violationForAlias(context, "alias-trailing-slash", alias);
        }

        if (urlValidationUtils.pathElementsContainInvalidCharacter(alias)) {
            return violationForAlias(context, "alias-invalid-chars", alias);
        }

        String clash = urlValidationUtils.aliasClashesWithSlug(alias, context.getDocumentNode());
        if (clash != null) {
            return violationForAliasClash(context, "alias-slug-clash", alias, clash);
        }

        clash = urlValidationUtils.aliasClashesWithAnotherAlias(alias, context.getDocumentNode());
        if (clash != null) {
            return violationForAliasClash(context, "alias-clash", alias, clash);
        }

        clash = urlValidationUtils.aliasClashesWithCategory(alias, context.getDocumentNode());
        if (clash != null) {
            return violationForAliasClash(context, "alias-category-clash", alias, clash);
        }

        return null;
    }

    Violation violationForAlias(ValidationContext context, String key, String alias) {
        Map<String, String> params = new HashMap<>();
        params.put("alias", alias);
        return context.createViolation(key, params);
    }

    Violation violationForAliasClash(ValidationContext context, String key, String alias, String clash) {
        Map<String, String> params = new HashMap<>();
        params.put("alias", alias);
        params.put("clash", clash);
        return context.createViolation(key, params);
    }

}