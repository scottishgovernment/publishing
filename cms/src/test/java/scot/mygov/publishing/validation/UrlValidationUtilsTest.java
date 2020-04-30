package scot.mygov.publishing.validation;

import org.junit.Test;
import scot.mygov.publishing.test.TestUtil;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlValidationUtilsTest {

    UrlValidationUtils sut = new UrlValidationUtils();

    @Test
    public void containsInvalidCharactersAcceptsExpectedStrings() {
        List<String> inputs = new ArrayList<>();
        Collections.addAll(inputs, "validslug", "blah");
        List<String> rejects = inputs.stream().filter(sut::containsInvalidCharacters).collect(toList());
        assertEquals(emptyList(), rejects);
    }

    @Test
    public void containsInvalidCharactersRejectsExpectedStrings() {
        List<String> inputs = new ArrayList<>();
        Collections.addAll(inputs, "SLUG", "S-lug", ", slug", "col:on", "slug/withslah", "..slug", "slug/./", "#");
        List<String> rejects = inputs.stream().filter(sut::containsInvalidCharacters).collect(toList());
        assertEquals(inputs, rejects);
    }

    @Test
    public void pathElementsContainInvalidCharacterRejectExpected() {
        List<String> inputs = new ArrayList<>();
        Collections.addAll(inputs, "slug1/slug&2", "/slug1/slug&2/", "/sl ug1/slug2/");
        List<String> actual = inputs.stream().filter(sut::pathElementsContainInvalidCharacter).collect(toList());
        assertEquals(inputs, actual);
    }

    @Test
    public void pathElementsContainInvalidCharacterAcceptExepcted() {
        List<String> inputs = new ArrayList<>();
        Collections.addAll(inputs, "slug1/slug2", "/slug1/slug2/", "/slug1", "/slug1/slug2");
        List<String> actual = inputs.stream().filter(sut::pathElementsContainInvalidCharacter).collect(toList());
        assertEquals(emptyList(), actual);
    }

    @Test
    public void slugClashesReturnNullForNoClash() throws RepositoryException {

        // ARRANGE
        List<Node> nodes = singletonList(nodeWithParent("parentIdentifier"));

        Session session = sessionWithNodes(TestUtil.iterator(nodes));
        sut.sessionSupplier = () -> session;
        Node documentNode = documentNode();

        // ACT
        String actual = sut.slugClashes("slug", documentNode);

        // ASSERT
        assertEquals(null, actual);
    }

    @Test
    public void slugClashesReturnClashingPath() throws RepositoryException {
        // ARRANGE
        List<Node> nodes = singletonList(nodeWithParent("clashingId"));

        Session session = sessionWithNodes(TestUtil.iterator(nodes));
        sut.sessionSupplier = () -> session;
        Node documentNode = documentNode();

        // ACT
        String actual = sut.slugClashes("slug", documentNode);

        // ASSERT
        assertEquals("clashingId-path", actual);
    }

    @Test
    public void slugClashesWithAliasReturnsNullForNoClash() throws RepositoryException {

        // ARRANGE
        List<Node> nodes = singletonList(nodeWithParent("parentIdentifier"));

        Session session = sessionWithNodes(TestUtil.iterator(nodes));
        sut.sessionSupplier = () -> session;
        Node documentNode = documentNode();

        // ACT
        String actual = sut.slugClashesWithAlias("slug", documentNode);

        // ASSERT
        assertEquals("parentIdentifier-path", actual);
    }

    @Test
    public void slugClashesWithAliasReturnClashingPath() throws RepositoryException {

        // ARRANGE
        List<Node> nodes = singletonList(nodeWithParent("clashingId"));

        Session session = sessionWithNodes(TestUtil.iterator(nodes));
        sut.sessionSupplier = () -> session;
        Node documentNode = documentNode();

        // ACT
        String actual = sut.slugClashesWithAlias("slug", documentNode);

        // ASSERT
        assertEquals("clashingId-path", actual);
    }

    @Test
    public void aliasClashesWithSlugReturnsNullForNoClash() throws RepositoryException {

        // ARRANGE
        List<Node> nodes = singletonList(nodeWithParent("parentIdentifier"));

        Session session = sessionWithNodes(TestUtil.iterator(nodes));
        sut.sessionSupplier = () -> session;
        Node documentNode = documentNode();

        // ACT
        String actual = sut.aliasClashesWithSlug("slug", documentNode);

        // ASSERT
        assertEquals("parentIdentifier-path", actual);
    }

    @Test
    public void aliasClashesWithSlugReturnClashingPath() throws RepositoryException {
        // ARRANGE
        List<Node> nodes = singletonList(nodeWithParent("clashingId"));

        Session session = sessionWithNodes(TestUtil.iterator(nodes));
        sut.sessionSupplier = () -> session;
        Node documentNode = documentNode();

        // ACT
        String actual = sut.aliasClashesWithSlug("slug", documentNode);

        // ASSERT
        assertEquals("clashingId-path", actual);
    }

    @Test
    public void aliasClashesWithAnotherAliasReturnsNullForNoClash() throws RepositoryException {

        // ARRANGE
        List<Node> nodes = singletonList(nodeWithParent("parentIdentifier"));

        Session session = sessionWithNodes(TestUtil.iterator(nodes));
        sut.sessionSupplier = () -> session;
        Node documentNode = documentNode();

        // ACT
        String actual = sut.aliasClashesWithAnotherAlias("slug", documentNode);

        // ASSERT
        assertEquals(null, actual);
    }

    @Test
    public void aliasClashesWithAnotherAliasReturnClashingPath() throws RepositoryException {
        // ARRANGE
        List<Node> nodes = singletonList(nodeWithParent("clashingId"));

        Session session = sessionWithNodes(TestUtil.iterator(nodes));
        sut.sessionSupplier = () -> session;
        Node documentNode = documentNode();

        // ACT
        String actual = sut.aliasClashesWithAnotherAlias("slug", documentNode);

        // ASSERT
        assertEquals("clashingId-path", actual);
    }

    @Test
    public void slugClashesWithCategoryReturnsNullForNoClashNode() throws RepositoryException {

        // ARRANGE
        List<Node> nodes = singletonList(nodeWithParent("parentIdentifier"));

        Session session = sessionWithNodes(TestUtil.iterator(nodes));
        when(session.nodeExists("/content/documents/trading-nation/slug")).thenReturn(false);
        sut.sessionSupplier = () -> session;
        Node documentNode = documentNode();

        // ACT
        String actual = sut.slugClashesWithCategory("slug", documentNode);

        // ASSERT
        assertEquals(null, actual);
    }

    @Test
    public void slugClashesWithCategoryReturnsNullForClashNpodeNotACategory() throws RepositoryException {

        // ARRANGE
        List<Node> nodes = singletonList(nodeWithParent("parentIdentifier"));

        Session session = sessionWithNodes(TestUtil.iterator(nodes));
        when(session.nodeExists("/content/documents/trading-nation/slug")).thenReturn(true);
        Node clashNode = mock(Node.class);
        when(clashNode.isNodeType("hippostsd:folder")).thenReturn(false);
        sut.sessionSupplier = () -> session;
        Node documentNode = documentNode();

        // ACT
        String actual = sut.slugClashesWithCategory("slug", documentNode);

        // ASSERT
        assertEquals(null, actual);
    }

    @Test
    public void slugClashesWithCategoryReturnClashingPath() throws RepositoryException {
        // ARRANGE
        List<Node> nodes = singletonList(nodeWithParent("clashingId"));

        Session session = sessionWithNodes(TestUtil.iterator(nodes));
        when(session.nodeExists("/content/documents/trading-nation/slug")).thenReturn(true);
        Node clashFolder = mock(Node.class);
        Node clashHandle = mock(Node.class);
        Node clashNode = mock(Node.class);
        when(clashFolder.getPath()).thenReturn("clashPath");
        when(clashFolder.isNodeType("hippostd:folder")).thenReturn(true);
        when(clashFolder.getNode("index")).thenReturn(clashHandle);
        when(clashHandle.getName()).thenReturn("clash");
        when(clashHandle.getNodes("clash")).thenReturn(TestUtil.iterator(singletonList(clashNode)));
        when(session.getNode("/content/documents/trading-nation/slug")).thenReturn(clashFolder);
        when(clashFolder.isNodeType("hippostd:folder")).thenReturn(true);
        when(clashNode.isNodeType("publishing:category")).thenReturn(true);

        sut.sessionSupplier = () -> session;
        Node documentNode = documentNode();

        // ACT
        String actual = sut.slugClashesWithCategory("slug", documentNode);

        // ASSERT
        assertEquals("clashPath", actual);
    }

    Node documentNode() throws RepositoryException {
        Node node = mock(Node.class);
        Node parent = mock(Node.class);
        Node siteAncestor = mock(Node.class);
        when(node.getParent()).thenReturn(parent);
        when(parent.getIdentifier()).thenReturn("parentIdentifier");
        when(siteAncestor.getPath()).thenReturn("/content/documents/trading-nation");
        when(node.getAncestor(3)).thenReturn(siteAncestor);
        return node;
    }

    Node nodeWithParent(String parentId) throws RepositoryException {
        Node node = mock(Node.class);
        Node parent = mock(Node.class);
        when(parent.getPath()).thenReturn(parentId + "-path");
        when(parent.getIdentifier()).thenReturn(parentId);
        when(node.getParent()).thenReturn(parent);
        return node;
    }

    Session sessionWithNodes(NodeIterator iterator) throws RepositoryException {
        Session session = mock(Session.class);
        Workspace workspace = mock(Workspace.class);
        QueryManager queryManager = mock(QueryManager.class);
        Query query = mock(Query.class);
        QueryResult result = mock(QueryResult.class);
        when(session.getWorkspace()).thenReturn(workspace);
        when(workspace.getQueryManager()).thenReturn(queryManager);
        when(queryManager.createQuery(any(), eq(Query.SQL))).thenReturn(query);
        when(query.execute()).thenReturn(result);
        when(result.getNodes()).thenReturn(iterator);
        when(session.nodeExists(any())).thenReturn(true);
        return session;
    }
}
