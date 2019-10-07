package scot.mygov.publishing.validation;

import org.junit.Test;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.ValidationContextException;
import org.onehippo.cms.services.validation.api.Violation;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static scot.mygov.publishing.validation.MinimumLengthValidator.MIN_LENGTH_KEY;

/**
 * Created by z418868 on 07/10/2019.
 */
public class MinimumLengthValidatorTest {

    @Test(expected= ValidationContextException.class)
    public void constructorThrowsExceptionIfNegativeMin() throws RepositoryException {
        Node config = config("-11");
        new MinimumLengthValidator(config);
    }

    @Test(expected= ValidationContextException.class)
    public void constructorThrowsExceptionIfNoMin() throws RepositoryException {
        Node config = config(null);
        new MinimumLengthValidator(config);
    }

    @Test(expected= ValidationContextException.class)
    public void constructorThrowsThrowsExceptionIfMinNotValidInteger() throws RepositoryException {
        Node config = config("thirty");
        new MinimumLengthValidator(config);
    }

    @Test(expected= ValidationContextException.class)
    public void constructorThrowsRethrowsExceptionIfRepoExThrown() throws RepositoryException {
        Node config = repoExceptionThrowingConfig();
        new MinimumLengthValidator(config);
    }

    @Test
    public void noViolationsIfMoreThanMin() throws RepositoryException {
        Node config = config("3");
        MinimumLengthValidator sut = new MinimumLengthValidator(config);
        assertFalse(sut.validate(null, "12345").isPresent());
        assertFalse(sut.validate(null, "1234567").isPresent());
        assertFalse(sut.validate(null, "1234567890").isPresent());
    }

    @Test
    public void violationIfTooShortEmptyOrNUll() throws RepositoryException {
        Node config = config("5");
        MinimumLengthValidator sut = new MinimumLengthValidator(config);
        assertTrue(sut.validate(context(), "12").isPresent());
        assertTrue(sut.validate(context(), "").isPresent());
        assertTrue(sut.validate(context(), null).isPresent());
    }

    Node config(String min) throws RepositoryException {
        Node config = mock(Node.class);
        Property minProp = stringProperty(min);
        when(config.hasProperty(MIN_LENGTH_KEY)).thenReturn(min != null);
        when(config.getProperty(MIN_LENGTH_KEY)).thenReturn(minProp);
        return config;
    }

    Property stringProperty(String value) throws RepositoryException {
        Property property = mock(Property.class);
        when(property.getString()).thenReturn(value);
        return property;
    }

    Node repoExceptionThrowingConfig() throws RepositoryException {
        Node config = mock(Node.class);
        when(config.hasProperty(anyString())).thenThrow(new RepositoryException());
        return config;
    }

    ValidationContext context() {
        ValidationContext context = mock(ValidationContext.class);
        Violation violation = mock(Violation.class);
        when(context.createViolation(anyMap())).thenReturn(violation);
        return context;
    }
}
