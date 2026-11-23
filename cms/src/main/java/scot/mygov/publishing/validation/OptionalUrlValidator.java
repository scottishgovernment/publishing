package scot.mygov.publishing.validation;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;


public class OptionalUrlValidator implements Validator<String> {

    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https", "ftp");

    public Optional<Violation> validate(ValidationContext context, String value) {
        if (StringUtils.isBlank(value)) {
            return Optional.empty();
        }

        return isValidUrl(value)
                ? Optional.empty()
                : Optional.of(context.createViolation());
    }

    static boolean isValidUrl(String value) {
        try {
            URI uri = new URI(value);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            return scheme != null
                    && ALLOWED_SCHEMES.contains(scheme.toLowerCase())
                    && host != null
                    && host.contains(".");
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
