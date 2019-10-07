package scot.mygov.publishing.validation;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.ValidationContextException;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;

import javax.jcr.Node;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Used to ensure fields satisfy a minimum length.
 */
public class MinimumLengthValidator implements Validator<String> {

    static final String MIN_LENGTH_KEY = "minlength.min";

    private int minLength = -1;

    public MinimumLengthValidator(Node config) {
        minLength = ValidatorUtil.getIntegerPropertyFromConfig(config, MIN_LENGTH_KEY);

        if (minLength < 0) {
            throw new ValidationContextException(MIN_LENGTH_KEY + " must be >= 0");
        }
    }

    public Optional<Violation> validate(ValidationContext context, String value) {
        if (StringUtils.length(value) < minLength) {
            Map<String, String> params = new HashMap<>();
            params.put(MIN_LENGTH_KEY, Integer.toString(minLength));
            return Optional.of(context.createViolation(params));
        } else {
            return Optional.empty();
        }
    }
}