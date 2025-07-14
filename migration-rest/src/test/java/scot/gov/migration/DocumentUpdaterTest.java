package scot.gov.migration;

import jakarta.ws.rs.core.MultivaluedHashMap;
import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;
import org.mockito.Mockito;
import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.pojo.model.ContentNode;

import javax.jcr.*;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by z418868 on 25/06/2020.
 */
public class DocumentUpdaterTest {

    @Test
    public void publishesDocumentIfPublishTrue() {
        DocumentManager manager = Mockito.mock(DocumentManager.class);
        DocumentUpdater.publish(manager, true, "location");
        verify(manager).publishDocument("location");
    }

    @Test
    public void doesNotPublishDocumentIfPublishFalse() {
        DocumentManager manager = Mockito.mock(DocumentManager.class);
        DocumentUpdater.publish(manager, false, "location");
        verify(manager, never()).publishDocument("location");
    }

    @Test
    public void localisedNameReturnsHippoNameIfPresent() {
        ContentNode node = new ContentNode();
        node.setName("name");
        node.setProperty("hippo:name", "hippo name");
        assertEquals("hippo name", DocumentUpdater.localizedName(node));
    }

    @Test
    public void localisedNameReturnsNameIfNoHippoNamePresent() {
        ContentNode node = new ContentNode();
        node.setName("name");
        assertEquals("name", DocumentUpdater.localizedName(node));
    }

    @Test
    public void removeAutoCreatedIndexIfPresentRemovesIfPresent() throws RepositoryException {
        Session session = Mockito.mock(Session.class);
        String actual = new DocumentUpdater().removeAutoCreatedIndexIfPresent(session, "location[2]");
        verify(session).removeItem("location[2]");
        assertEquals("location", actual);
    }

    @Test
    public void removeAutoCreatedIndexIfPresentIfNotPresent() throws RepositoryException {
        Session session = Mockito.mock(Session.class);
        String actual = new DocumentUpdater().removeAutoCreatedIndexIfPresent(session, "location");
        verify(session, never()).removeItem(anyString());
        assertEquals("location", actual);
    }


    @Test
    public void setAuthorshipSetsExpectedFields() throws RepositoryException {

        Session session = mock(Session.class);
        List<Node> nodes = new ArrayList<>();
        nodes.add(nodeWithState("draft"));
        nodes.add(nodeWithState("unpublished"));
        nodes.add(nodeWithState("published"));
        Node node = mock(Node.class);
        when(node.getNodes()).thenReturn(iterator(nodes));
        when(session.getNode("location")).thenReturn(node);

        MultivaluedHashMap<String, String> map = new MultivaluedHashMap();
        map.put("createdBy", singletonList("createdBy"));
        map.put("created", singletonList("2010-01-01 00:00:00"));
        map.put("lastModifiedBy", singletonList("lastModifiedBy"));
        map.put("lastModified", singletonList("2010-01-01 00:00:00"));
        ContentAuthorship authorship = ContentAuthorship.newInstance(map);
        new DocumentUpdater().setAuthorship(session, "location", authorship);
    }

    Node nodeWithState(String state) throws RepositoryException {
        Node node = mock(Node.class);
        Property stateProp = mock(Property.class);
        when(stateProp.getString()).thenReturn(state);
        when(node.getProperty("hippostd:state")).thenReturn(stateProp);
        return node;
    }

    public static NodeIterator iterator(Node ...nodes) {
        return iterator(Arrays.asList(nodes));
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

}
