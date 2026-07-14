package scot.mygov.publishing.validation;

import org.junit.Test;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static scot.mygov.publishing.validation.PreviewTokenValidator.CONTENT_PROPERTY;

public class PreviewTokenValidatorTest {

    PreviewTokenValidator sut = new PreviewTokenValidator();

    ValidationContextImpl context = new ValidationContextImpl();

    // --- validate(context, node) ---

    @Test
    public void noViolationWhenNodeHasNoContentProperty() throws RepositoryException {
        Node node = mock(Node.class);
        when(node.hasProperty(CONTENT_PROPERTY)).thenReturn(false);
        assertFalse(sut.validate(context, node).isPresent());
    }

    @Test
    public void noViolationWhenRepositoryExceptionThrown() throws RepositoryException {
        Node node = mock(Node.class);
        when(node.hasProperty(CONTENT_PROPERTY)).thenThrow(new RepositoryException());
        assertFalse(sut.validate(context, node).isPresent());
    }

    @Test
    public void noViolationWhenContentHasNoLinks() throws RepositoryException {
        assertFalse(sut.validate(context, nodeWithContent("<p>No links here</p>")).isPresent());
    }

    @Test
    public void violationWhenContentHasLinkWithPreviewToken() throws RepositoryException {
        String html = "<p><a href=\"https://www.mygov.scot/page?preview_token=abc123\">link</a></p>";
        assertTrue(sut.validate(context, nodeWithContent(html)).isPresent());
    }

    // --- validateHtml ---

    @Test
    public void noViolationForNullHtml() {
        assertFalse(sut.validateHtml(context, null).isPresent());
    }

    @Test
    public void noViolationForEmptyHtml() {
        assertFalse(sut.validateHtml(context, "").isPresent());
    }

    @Test
    public void noViolationForHtmlWithNoLinks() {
        assertFalse(sut.validateHtml(context, "<p>No links here</p>").isPresent());
    }

    @Test
    public void noViolationForLinkWithNoQueryString() {
        assertFalse(sut.validateHtml(context, "<p><a href=\"https://www.mygov.scot/page\">link</a></p>").isPresent());
    }

    @Test
    public void noViolationForLinkWithOtherQueryParams() {
        assertFalse(sut.validateHtml(context, "<p><a href=\"https://www.mygov.scot/page?foo=bar&baz=qux\">link</a></p>").isPresent());
    }

    @Test
    public void violationWhenLinkContainsPreviewToken() {
        assertTrue(sut.validateHtml(context, "<p><a href=\"https://www.mygov.scot/page?preview_token=abc123\">link</a></p>").isPresent());
    }

    @Test
    public void violationWhenPreviewTokenIsFirstQueryParam() {
        assertTrue(sut.validateHtml(context, "<a href=\"/page?preview_token=abc&other=val\">link</a>").isPresent());
    }

    @Test
    public void violationWhenPreviewTokenIsLastQueryParam() {
        assertTrue(sut.validateHtml(context, "<a href=\"/page?other=val&preview_token=abc\">link</a>").isPresent());
    }

    @Test
    public void violationWhenOneOfMultipleLinksContainsPreviewToken() {
        String html = "<p><a href=\"/page\">safe</a> and <a href=\"/other?preview_token=xyz\">unsafe</a></p>";
        assertTrue(sut.validateHtml(context, html).isPresent());
    }

    @Test
    public void noViolationForLinkWithSimilarParamName() {
        assertFalse(sut.validateHtml(context, "<a href=\"/page?my_preview_token=abc\">link</a>").isPresent());
        assertFalse(sut.validateHtml(context, "<a href=\"/page?preview_token_extra=abc\">link</a>").isPresent());
        assertFalse(sut.validateHtml(context, "<a href=\"/page?preview=abc\">link</a>").isPresent());
    }

    // --- containsPreviewToken ---

    @Test
    public void containsPreviewTokenReturnsFalseForNull() {
        assertFalse(sut.containsPreviewToken(null));
    }

    @Test
    public void containsPreviewTokenReturnsFalseForHrefWithNoQuery() {
        assertFalse(sut.containsPreviewToken("/page"));
    }

    @Test
    public void containsPreviewTokenReturnsTrueForMatchingParam() {
        assertTrue(sut.containsPreviewToken("/page?preview_token=abc"));
        assertTrue(sut.containsPreviewToken("/page?foo=bar&preview_token=abc"));
        assertTrue(sut.containsPreviewToken("/page?preview_token=abc&foo=bar"));
    }

    @Test
    public void containsPreviewTokenReturnsFalseForNonMatchingParams() {
        assertFalse(sut.containsPreviewToken("/page?foo=bar"));
        assertFalse(sut.containsPreviewToken("/page?preview=abc"));
        assertFalse(sut.containsPreviewToken("/page?my_preview_token=abc"));
    }

    // --- helpers ---

    Node nodeWithContent(String html) throws RepositoryException {
        Node node = mock(Node.class);
        Property property = mock(Property.class);
        when(property.getString()).thenReturn(html);
        when(node.hasProperty(CONTENT_PROPERTY)).thenReturn(true);
        when(node.getProperty(CONTENT_PROPERTY)).thenReturn(property);
        return node;
    }
}
