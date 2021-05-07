package scot.mygov.publishing.validation;

import org.junit.Test;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Violation;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.contentbean.ValueListItem;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Collections;
import java.util.Optional;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrganisationTagValidatorTest {

    @Test
    public void acceptsValidOrganisationTag() throws RepositoryException {

        // ARRANGE
        ValidationContext context = validationContext();
        OrganisationTagValidator sut = setupValidator();

        // ACT
        Optional<Violation> violation = sut.validate(context, "valid-organisation");

        // ASSERT
        assertFalse(violation.isPresent());
    }

    @Test
    public void invalidOrganisationTagCreatesViolation() throws RepositoryException {
        // ARRANGE
        OrganisationTagValidator sut = setupValidator();
        ValidationContext context = validationContext();

        // ACT
        Optional<Violation> violation = sut.validate(context, "invalid-organisation");

        // ASSERT
        assertTrue(violation.isPresent());
    }

    OrganisationTagValidator setupValidator() throws RepositoryException {
        HippoUtils hippoUtils = mock(HippoUtils.class);
        OrganisationTagValidator validator = new OrganisationTagValidator(hippoUtils);
        ValueList valueList = mock(ValueList.class);
        ValueListItem valueListItem = mock(ValueListItem.class);
        valueListItem.setName("valid-organisation");
        when(validator.hippoUtils.getValueList(
                any(), anyString())).
                thenReturn(valueList);
        when(valueList.getItems()).thenReturn(Collections.singletonList(valueListItem));
        when(valueListItem.getLabel()).thenReturn("valid-organisation");

        return validator;
    }

    ValidationContext validationContext() throws RepositoryException {
        Node documentNode = mock(Node.class);
        Session session = mock(Session.class);
        when(documentNode.getSession()).thenReturn(session);

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
