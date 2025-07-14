package scot.mygov.publishing;

import org.apache.commons.lang.NotImplementedException;
import org.hippoecm.repository.HippoStdNodeType;
import org.junit.Test;
import org.mockito.Mockito;
import scot.mygov.publishing.test.TestUtil;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.*;

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
    public void findDraftVariant() throws RepositoryException {
        // ARRANGE
        List<Node> children = new ArrayList<>();
        Node draftNode = draftNode();
        Collections.addAll(children, unpublishedNode(), draftNode);
        NodeIterator nodeIterator = TestUtil.iterator(children);

        Node handle = mock(Node.class);
        when(handle.getName()).thenReturn("name");
        when(handle.getNodes("name")).thenReturn(nodeIterator);

        // ACT
        Node actual = new HippoUtils().getDraftVariant(handle);

        // ASSERT
        assertSame(actual, draftNode);
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

    @Test
    public void executeQueryCallsConsumerWithResults() throws RepositoryException {

        // ARRANGE
        List<String> nodeIds = new ArrayList<>();
        Collections.addAll(nodeIds, "one", "two", "three");
        List<Node> nodes = new ArrayList<>();
        for (String id: nodeIds) {
            nodes.add(node(id));
        }

        HippoUtils sut = new HippoUtils();
        List<String> collectedIds = new ArrayList<>();
        HippoUtils.ThrowingConsumer consumer = new HippoUtils.ThrowingConsumer() {
            public void accept(Node t) throws RepositoryException {
                collectedIds.add(t.getIdentifier());
            }
        };

        // ACT
        sut.executeXpathQuery(sessionWithNodes(nodes), "query", consumer);

        // ASSERT
        assertEquals(collectedIds, nodeIds);
    }

    Node node(String id) throws RepositoryException {
        Node node = mock(Node.class);
        when(node.getIdentifier()).thenReturn(id);
        return node;
    }

    public static Session sessionWithNodes(List<Node> nodes) throws RepositoryException {
        Session session = mock(Session.class);
        Workspace workspace = mock(Workspace.class);
        QueryManager queryManager = mock(QueryManager.class);
        Query query = mock(Query.class);
        QueryResult queryResult = mock(QueryResult.class);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(any(), any())).thenReturn(query);
        when(query.execute()).thenReturn(queryResult);
        when(queryResult.getNodes()).thenReturn(iterator(nodes));
        return session;
    }

    public static NodeIterator iterator(Collection<Node> nodes) {

        Iterator<Node> it = nodes.iterator();

        return new NodeIterator() {

            public boolean hasNext() {
                return  it.hasNext();
            }

            public Node nextNode() {
                return (Node) it.next();
            }

            public void skip(long skipNum) {
                throw new NotImplementedException();
            }

            public long getSize() {
                return nodes.size();
            }

            public long getPosition() {
                throw new NotImplementedException();
            }

            public NodeIterator next() {
                throw new NotImplementedException();
            }
        };
    }

    Node unpublishedNode() throws RepositoryException {
        return nodeOfState(HippoStdNodeType.UNPUBLISHED);
    }

    Node publishedNode() throws RepositoryException {
        return nodeOfState(HippoStdNodeType.PUBLISHED);
    }

    Node draftNode() throws RepositoryException {
        return nodeOfState(HippoStdNodeType.DRAFT);
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
