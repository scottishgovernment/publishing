package scot.mygov.publishing;

import org.hippoecm.repository.HippoStdNodeType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
        Node actual = HippoUtils.getPublishedVariant(handle);

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
        Node actual = HippoUtils.getPublishedVariant(handle);

        // ASSERT
        assertNull(actual);
    }

    @Test
    public void ensureMixinAddsMixinIfAbsent() throws RepositoryException {

        // ARRANGE
        Node node = mock(Node.class);
        when(node.isNodeType(any())).thenReturn(false);

        // ACT
        HippoUtils.ensureHasMixin(node, "mixin");

        // ASSERT
        verify(node, Mockito.times(1)).addMixin("mixin");
    }

    @Test
    public void ensureMixinIgnoresIfAbsent() throws RepositoryException {
        // ARRANGE
        Node node = mock(Node.class);
        when(node.isNodeType(any())).thenReturn(true);

        // ACT
        HippoUtils.ensureHasMixin(node, "mixin");

        // ASSERT
        verify(node, never()).addMixin(any());
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
        when(node.getProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(stateProperty);
        return node;
    }
}
