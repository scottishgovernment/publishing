package scot.mygov.publishing.validation;

import org.onehippo.cms.services.validation.api.ValidationContextException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

/**
 * Created by z418868 on 07/10/2019.
 */
public class ValidatorUtil {

    private ValidatorUtil() {
        // prevent instance creation
    }

    static int getIntegerPropertyFromConfig(Node config, String property) {
        String strValue = "";
        try {
            if (!config.hasProperty(property)) {
                throw new ValidationContextException(String.format("Config has no %s property", property));
            }
            strValue = config.getProperty(property).getString();
            return Integer.valueOf(strValue);
        } catch (RepositoryException e) {
            throw new ValidationContextException(String.format("Could not read value of property %s", property), e);
        } catch (NumberFormatException e) {
            throw new ValidationContextException(String.format("Invalid value of property %s (%s)", property, strValue), e);
        }
    }
}
