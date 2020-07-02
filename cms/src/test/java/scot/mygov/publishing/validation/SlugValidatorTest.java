package scot.mygov.publishing.validation;

import org.junit.Test;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Violation;

import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SlugValidatorTest {

    @Test
    public void acceptsValidSlugs() throws RepositoryException {

        // ARRANGE
        SlugValidator sut = new SlugValidator();
        sut.urlValidationUtils = validationUtils();
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violation = sut.validate(context, "valid-slug");

        // ASSERT
        assertFalse(violation.isPresent());
    }

    @Test
    public void repoExceptionCreatesViolation() throws RepositoryException {
        // ARRANGE
        SlugValidator sut = new SlugValidator();
        sut.urlValidationUtils = exceptionThrowingValidationUtils();
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violation = sut.validate(context, "slug");

        // ASSERT
        assertTrue(violation.isPresent());
    }

    @Test
    public void correctViolationForInvalidCharacter() throws RepositoryException {
        // ARRANGE
        SlugValidator sut = new SlugValidator();
        ValidationContext context = validationContext();
        sut.urlValidationUtils = validationUtils();
        when(sut.urlValidationUtils.containsInvalidCharacters(any())).thenReturn(true);

        // ACT
        Optional<Violation> violation = sut.validate(context, "slug");

        // ASSERT
        assertTrue(violation.isPresent());
        assertEquals("slug-invalid-chars", ((ViolationImpl) violation.get()).getSubkey());
    }

    @Test
    public void correctViolationForSlugClashes() throws RepositoryException {
        // ARRANGE
        SlugValidator sut = new SlugValidator();
        sut.urlValidationUtils = validationUtils();
        ValidationContext context = validationContext();
        when(sut.urlValidationUtils.slugClashes(eq("slug"), any())).thenReturn("clash");

        // ACT
        Optional<Violation> violationOptional = sut.validate(context, "slug");

        // ASSERT
        assertTrue(violationOptional.isPresent());
        ViolationImpl violation = ((ViolationImpl) violationOptional.get());
        assertEquals("slug-clash", violation.getSubkey());
        assertEquals(violation.getParameters(), singletonMap("clash", "clash"));
    }

    @Test
    public void correctViolationForClashWithAlias() throws RepositoryException {
        // ARRANGE
        SlugValidator sut = new SlugValidator();
        sut.urlValidationUtils = validationUtils();
        ValidationContext context = validationContext();
        when(sut.urlValidationUtils.slugClashesWithAlias(eq("slug"), any())).thenReturn("clash");

        // ACT
        Optional<Violation> violationOptional = sut.validate(context, "slug");

        // ASSERT
        assertTrue(violationOptional.isPresent());
        ViolationImpl violation = ((ViolationImpl) violationOptional.get());
        assertEquals("slug-alias-clash", violation.getSubkey());
        assertEquals(violation.getParameters(), singletonMap("clash", "clash"));
    }

    @Test
    public void correctViolationForClashWithCategory() throws RepositoryException {
        // ARRANGE
        SlugValidator sut = new SlugValidator();
        sut.urlValidationUtils = validationUtils();
        ValidationContext context = validationContext();
        when(sut.urlValidationUtils.slugClashesWithCategory(eq("slug"), any())).thenReturn("clash");

        // ACT
        Optional<Violation> violationOptional = sut.validate(context, "slug");

        // ASSERT
        assertTrue(violationOptional.isPresent());
        ViolationImpl violation = ((ViolationImpl) violationOptional.get());
        assertEquals("slug-category-clash", violation.getSubkey());
        assertEquals(singletonMap("clash", "clash"), violation.getParameters());
    }

    UrlValidationUtils validationUtils() throws RepositoryException {
        UrlValidationUtils urlValidationUtils = mock(UrlValidationUtils.class);
        when(urlValidationUtils.containsInvalidCharacters(any())).thenReturn(false);
        when(urlValidationUtils.slugClashes(any(), any())).thenReturn(null);
        when(urlValidationUtils.slugClashesWithAlias(any(), any())).thenReturn(null);
        when(urlValidationUtils.slugClashesWithCategory(any(), any())).thenReturn(null);
        return urlValidationUtils;
    }

    UrlValidationUtils exceptionThrowingValidationUtils() throws RepositoryException {
        UrlValidationUtils urlValidationUtils = mock(UrlValidationUtils.class);
        when(urlValidationUtils.slugClashes(any(), any())).thenThrow(new RepositoryException(""));
        return urlValidationUtils;
    }

    ValidationContext validationContext() throws RepositoryException {
        return validationContext("publishing:article");
    }

    ValidationContext validationContext(String type) throws RepositoryException {
        Node documentNode = mock(Node.class);
        NodeType nodetype = mock(NodeType.class);
        when(nodetype.getName()).thenReturn(type);
        when(documentNode.getPrimaryNodeType()).thenReturn(nodetype);
        return new TestContext(documentNode);
    }

    class TestContext extends ValidationContextImpl {

        Node documentNode;

        TestContext(Node documentNode) {
            this.documentNode = documentNode;
        }

        @Override
        public Node getDocumentNode() {
            return documentNode;
        }
    }
}
