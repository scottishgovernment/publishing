package scot.mygov.publishing.validation;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;

import java.util.Optional;


public class OptionalUrlValidator implements Validator<String> {

    public Optional<Violation> validate(ValidationContext context, String value) {
        if (StringUtils.isBlank(value)) {
            return Optional.empty();
        }

        return UrlValidator.getInstance().isValid(value)
                ? Optional.empty()
                : Optional.of(context.createViolation());
    }
}
