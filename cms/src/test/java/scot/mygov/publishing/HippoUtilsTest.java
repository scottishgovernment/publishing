package scot.mygov.publishing;

import org.hippoecm.repository.HippoStdNodeType;
import org.junit.Test;
import org.mockito.Mockito;
import scot.mygov.publishing.test.TestUtil;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by z418868 on 10/03/2020.
 */
public class HippoUtilsTest {

    @Test
    public void findPublishedVariant() throws RepositoryException {
        // ARRANGE
        List<Node> children = new ArrayList<>();
        Node publishedNode = publishedNode();
        Collections.addAll(children, unpublishedNode(), publishedNode);
        NodeIterator nodeIterator = TestUtil.iterator(children);

        Node handle = mock(Node.class);
        when(handle.getName()).thenReturn("name");
        when(handle.getNodes("name")).thenReturn(nodeIterator);

        // ACT
        Node actual = new HippoUtils().getPublishedVariant(handle);

        // ASSERT
        assertSame(actual, publishedNode);
    }

    @Test
    public void returnsNullIfNoPublishedVariant() throws RepositoryException {
        // ARRANGE
        List<Node> children = new ArrayList<>();
        Collections.addAll(children, unpublishedNode(), unpublishedNode());
        NodeIterator nodeIterator = TestUtil.iterator(children);

        Node handle = mock(Node.class);
        when(handle.getName()).thenReturn("name");
        when(handle.getNodes("name")).thenReturn(nodeIterator);

        // ACT
        Node actual = new HippoUtils().getPublishedVariant(handle);

        // ASSERT
        assertNull(actual);
    }

    @Test
    public void ensureMixinAddsMixinIfAbsent() throws RepositoryException {

        // ARRANGE
        Node node = mock(Node.class);
        when(node.isNodeType(any())).thenReturn(false);

        // ACT
        new HippoUtils().ensureHasMixin(node, "mixin");

        // ASSERT
        verify(node, Mockito.times(1)).addMixin("mixin");
    }

    @Test
    public void ensureMixinIgnoresIfAbsent() throws RepositoryException {
        // ARRANGE
        Node node = mock(Node.class);
        when(node.isNodeType(any())).thenReturn(true);

        // ACT
        new HippoUtils().ensureHasMixin(node, "mixin");

        // ASSERT
        verify(node, never()).addMixin(any());
    }

    @Test
    public void isOneOfNodeTypesReturnfFalsIfNotOneOfSpecifiedTypes() throws RepositoryException {

        // ARRANGE
        Node node = mock(Node.class);
        when(node.isNodeType(any())).thenReturn(false);

        // ACT
        boolean actual = new HippoUtils().isOneOfNodeTypes(node, "type1", "type2");

        // ASSERT
        assertFalse(actual);
    }

    @Test
    public void isOneOfNodeTypesReturnfTrueIfOneOfSpecifiedTypes() throws RepositoryException {

        // ARRANGE
        Node node = mock(Node.class);
        when(node.isNodeType(eq("type1"))).thenReturn(false);
        when(node.isNodeType(eq("type2"))).thenReturn(true);

        // ACT
        boolean actual = new HippoUtils().isOneOfNodeTypes(node, "type1", "type2");

        // ASSERT
        assertTrue(actual);
    }

    Node unpublishedNode() throws RepositoryException {
        return nodeOfState(HippoStdNodeType.UNPUBLISHED);
    }

    Node publishedNode() throws RepositoryException {
        return nodeOfState(HippoStdNodeType.PUBLISHED);
    }

    Node nodeOfState(String state) throws RepositoryException {
        Node node = mock(Node.class);
        Property stateProperty = mock(Property.class);
        when(stateProperty.getString()).thenReturn(state);
        when(node.hasProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(true);
        when(node.getProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(stateProperty);
        return node;
    }


}
