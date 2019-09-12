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
import static scot.mygov.publishing.validation.WordCountValidator.WORK_COUNT_MAX_KEY;
import static scot.mygov.publishing.validation.WordCountValidator.WORK_COUNT_MIN_KEY;

/**
 * Created by z418868 on 12/09/2019.
 */
public class WordCountValidatorTest {

    @Test(expected= ValidationContextException.class)
    public void constructorThrowsExceptionIfNegativeMinWordCount() throws RepositoryException {
        Node config = config("-11", "19");
        new WordCountValidator(config);
    }

    @Test(expected= ValidationContextException.class)
    public void constructorThrowsExceptionIfNoMinWordCount() throws RepositoryException {
        Node config = config(null, "19");
        new WordCountValidator(config);
    }

    @Test(expected= ValidationContextException.class)
    public void constructorThrowsExceptionIfNoMaxWordCount() throws RepositoryException {
        Node config = config("3", null);
        new WordCountValidator(config);
    }

    @Test(expected= ValidationContextException.class)
    public void constructorThrowsExceptionIfMinGreaterThanMax() throws RepositoryException {
        Node config = config("30", "10");
        new WordCountValidator(config);
    }

    @Test(expected= ValidationContextException.class)
    public void constructorThrowsRethrowsExceptionIfRepoExThrown() throws RepositoryException {
        Node config = repoExceptionThrowingConfig();
        new WordCountValidator(config);
    }

    @Test(expected= ValidationContextException.class)
    public void constructorThrowsThrowsExceptionIfMinNotValidInteger() throws RepositoryException {
        Node config = config("thirty", "10");
        new WordCountValidator(config);
    }

    @Test(expected= ValidationContextException.class)
    public void constructorThrowsThrowsExceptionIfMaxNotValidInteger() throws RepositoryException {
        Node config = config("1", "1.20");
        new WordCountValidator(config);
    }

    @Test
    public void noViolationsIfWithinLimits() throws RepositoryException {
        Node config = config("5", "10");
        WordCountValidator sut = new WordCountValidator(config);
        assertFalse(sut.validate(null, "one two three four five").isPresent());
        assertFalse(sut.validate(null, "one two three four five six").isPresent());
        assertFalse(sut.validate(null, "one two three four five six seven eighth nine ten").isPresent());
    }

    @Test
    public void violationIfTooShortEmptyOrNUll() throws RepositoryException {
        Node config = config("5", "10");
        WordCountValidator sut = new WordCountValidator(config);
        assertTrue(sut.validate(context(), "one two three four").isPresent());
        assertTrue(sut.validate(context(), "one").isPresent());
        assertTrue(sut.validate(context(), "").isPresent());
        assertTrue(sut.validate(context(), null).isPresent());
    }

    @Test
    public void violationsIfTooLong()  throws RepositoryException {
        Node config = config("1", "5");
        WordCountValidator sut = new WordCountValidator(config);
        assertTrue(sut.validate(context(), "one two three four five six").isPresent());
    }

    Node config(String min, String max) throws RepositoryException {
        Node config = mock(Node.class);
        Property minProp = stringProperty(min);
        Property maxProp = stringProperty(max);
        when(config.hasProperty(WORK_COUNT_MIN_KEY)).thenReturn(min != null);
        when(config.getProperty(WORK_COUNT_MIN_KEY)).thenReturn(minProp);
        when(config.hasProperty(WORK_COUNT_MAX_KEY)).thenReturn(max != null);
        when(config.getProperty(WORK_COUNT_MAX_KEY)).thenReturn(maxProp);
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
