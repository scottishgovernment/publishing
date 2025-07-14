package scot.mygov.publishing.validation;

import org.junit.Test;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Violation;

import javax.jcr.RepositoryException;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlAliasValidatorTest {

    @Test
    public void acceptsValidSlugs() throws RepositoryException {

        // ARRANGE
        UrlAliasValidator sut = new UrlAliasValidator();
        sut.urlValidationUtils = validationUtils();
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violation = sut.validate(context, "/valid-slug");

        // ASSERT
        assertFalse(violation.isPresent());
    }

    @Test
    public void repoExceptionCreatesViolation() throws RepositoryException {
        // ARRANGE
        UrlAliasValidator sut = new UrlAliasValidator();
        sut.urlValidationUtils = exceptionThrowingValidationUtils();
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violation = sut.validate(context, "/valid-slug");

        // ASSERT
        assertTrue(violation.isPresent());
    }

    @Test
    public void rejectsEmptyAlias() throws RepositoryException {

        // ARRANGE
        UrlAliasValidator sut = new UrlAliasValidator();
        sut.urlValidationUtils = validationUtils();
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violationOptional = sut.validate(context, "");

        // ASSERT
        assertTrue(violationOptional.isPresent());
        ViolationImpl violation = ((ViolationImpl) violationOptional.get());
        assertEquals("alias-empty", violation.getSubkey());
    }
    @Test
    public void rejectsAliasWithNoLeadingSlash() throws RepositoryException {

        // ARRANGE
        UrlAliasValidator sut = new UrlAliasValidator();
        sut.urlValidationUtils = validationUtils();
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violationOptional = sut.validate(context, "no-leading-slash");

        // ASSERT
        assertTrue(violationOptional.isPresent());
        ViolationImpl violation = ((ViolationImpl) violationOptional.get());
        assertEquals("alias-leading-slash", violation.getSubkey());
    }

    @Test
    public void rejectsAliasWithNoTrailingSlash() throws RepositoryException {

        // ARRANGE
        UrlAliasValidator sut = new UrlAliasValidator();
        sut.urlValidationUtils = validationUtils();
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violationOptional = sut.validate(context, "/trailing-slash/");

        // ASSERT
        assertTrue(violationOptional.isPresent());
        ViolationImpl violation = ((ViolationImpl) violationOptional.get());
        assertEquals("alias-trailing-slash", violation.getSubkey());
    }

    @Test
    public void rejectsAliasWithInvalidChars() throws RepositoryException {

        // ARRANGE
        UrlAliasValidator sut = new UrlAliasValidator();
        sut.urlValidationUtils = validationUtils();
        when(sut.urlValidationUtils.pathElementsContainInvalidCharacter(any())).thenReturn(true);
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violationOptional = sut.validate(context, "/trailing/$$");

        // ASSERT
        assertTrue(violationOptional.isPresent());
        ViolationImpl violation = ((ViolationImpl) violationOptional.get());
        assertEquals("alias-invalid-chars", violation.getSubkey());
    }

    @Test
    public void rejectsSlugClash() throws RepositoryException {

        // ARRANGE
        UrlAliasValidator sut = new UrlAliasValidator();
        sut.urlValidationUtils = validationUtils();
        when(sut.urlValidationUtils.aliasClashesWithSlug(any(), any())).thenReturn("clash");
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violationOptional = sut.validate(context, "/clashing-slug");

        // ASSERT
        assertTrue(violationOptional.isPresent());
        ViolationImpl violation = ((ViolationImpl) violationOptional.get());
        assertEquals("alias-slug-clash", violation.getSubkey());
        assertEquals("clash", violation.getParameters().get("clash"));
        assertEquals("/clashing-slug", violation.getParameters().get("alias"));
    }

    @Test
    public void rejectsAliasClash() throws RepositoryException {

        // ARRANGE
        UrlAliasValidator sut = new UrlAliasValidator();
        sut.urlValidationUtils = validationUtils();
        when(sut.urlValidationUtils.aliasClashesWithAnotherAlias(any(), any())).thenReturn("clash");
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violationOptional = sut.validate(context, "/clashing-alias");

        // ASSERT
        assertTrue(violationOptional.isPresent());
        ViolationImpl violation = ((ViolationImpl) violationOptional.get());
        assertEquals("alias-clash", violation.getSubkey());
        assertEquals("clash", violation.getParameters().get("clash"));
        assertEquals("/clashing-alias", violation.getParameters().get("alias"));
    }

    @Test
    public void rejectsCategoryClash() throws RepositoryException {

        // ARRANGE
        UrlAliasValidator sut = new UrlAliasValidator();
        sut.urlValidationUtils = validationUtils();
        when(sut.urlValidationUtils.aliasClashesWithCategory(any(), any())).thenReturn("clash");
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violationOptional = sut.validate(context, "/clashing-category");

        // ASSERT
        assertTrue(violationOptional.isPresent());
        ViolationImpl violation = ((ViolationImpl) violationOptional.get());
        assertEquals("alias-category-clash", violation.getSubkey());
        assertEquals("clash", violation.getParameters().get("clash"));
        assertEquals("/clashing-category", violation.getParameters().get("alias"));
    }

    UrlValidationUtils validationUtils() throws RepositoryException {
        UrlValidationUtils urlValidationUtils = mock(UrlValidationUtils.class);
        when(urlValidationUtils.pathElementsContainInvalidCharacter(any())).thenReturn(false);
        when(urlValidationUtils.aliasClashesWithAnotherAlias(any(), any())).thenReturn(null);
        when(urlValidationUtils.aliasClashesWithCategory(any(), any())).thenReturn(null);
        when(urlValidationUtils.aliasClashesWithSlug(any(), any())).thenReturn(null);
        return urlValidationUtils;
    }

    UrlValidationUtils exceptionThrowingValidationUtils() throws RepositoryException {
        UrlValidationUtils urlValidationUtils = mock(UrlValidationUtils.class);
        when(urlValidationUtils.aliasClashesWithAnotherAlias(any(), any())).thenThrow(new RepositoryException(""));
        return urlValidationUtils;
    }

    ValidationContext validationContext() {
        return new ValidationContextImpl();
    }
}
