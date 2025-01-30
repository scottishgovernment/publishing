package scot.gov.topics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Resource {

    private static final Logger LOG = LoggerFactory.getLogger(Resource.class);

    private static final String TOPICS = "topics";

    Session session;

    public Resource(Session session) {
        this.session = session;
    }

    @GET
    @Path("/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ListItem> getTopics(@PathParam("uuid") String uuid) {
        try {
            return getListItems(uuid);
        } catch (RepositoryException e) {
            LOG.error("Invalid item", e);
            return Collections.emptyList();
        }
    }

    List<ListItem> getListItems(String uuid) throws RepositoryException {
        // the node we want to get the topics for....
        Node node = session.getNodeByIdentifier(uuid);
        Node site = (Node) node.getAncestor(3);
        Node adminFolder = site.getNode("administration");
        if (!adminFolder.hasNode(TOPICS)) {
            return Collections.emptyList();
        }
        Node topics = adminFolder.getNode(TOPICS).getNode(TOPICS);
        NodeIterator it = topics.getNodes();
        List<ListItem> items = new ArrayList<>();
        while (it.hasNext()) {
            items.add(item( it.nextNode()));
        }
        return items;
    }

    ListItem item(Node item) throws RepositoryException {
        ListItem listitem = new ListItem();
        listitem.setLabel(item.getProperty("selection:label").getString());
        listitem.setKey(item.getProperty("selection:key").getString());
        return listitem;
    }

    class ListItem {

        private String label;

        private String key;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }
}
