package scot.gov.topics;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * resource used to provide site specific value lists, with a fallback to using the global one if the current site
 * does not provide specific values
 *
 * currently used for topics and publication types
 */
public class SiteSpecificResource {

    private static final Logger LOG = LoggerFactory.getLogger(SiteSpecificResource.class);

    private static final String VALUE_LIST_PATH = "/content/documents/publishing/valuelists";

    Session session;

    String name;

    public SiteSpecificResource(Session session, String name) {
        this.session = session;
        this.name = name;
    }

    @GET
    @Path("/{uuid}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<SiteSpecificResource.ListItem> getValues(@PathParam("uuid") String uuid) {
        try {
            return getListItemsForUuid(uuid);
        } catch (RepositoryException e) {
            LOG.error("Invalid item", e);
            return Collections.emptyList();
        }
    }

    List<SiteSpecificResource.ListItem> getListItemsForUuid(String uuid) throws RepositoryException {
        Node node = session.getNodeByIdentifier(uuid);
        Node site = (Node) node.getAncestor(3);
        Node adminFolder = site.getNode("administration");
        if (!adminFolder.hasNode(name)) {
            return getGlobalListItems();
        }
        Node topics = adminFolder.getNode(name).getNode(name);
        NodeIterator it = topics.getNodes();
        List<SiteSpecificResource.ListItem> items = new ArrayList<>();
        while (it.hasNext()) {
            items.add(item( it.nextNode()));
        }
        return items;
    }

    List<SiteSpecificResource.ListItem> getGlobalListItems() throws RepositoryException {
        Node valueLists = session.getNode(VALUE_LIST_PATH);
        if (!valueLists.hasNode(name)) {
            return Collections.emptyList();
        }
        return getListItems(valueLists.getNode(name).getPath());
    }

    List<SiteSpecificResource.ListItem> getListItems(String path) throws RepositoryException {
        Node valueList = session.getNode(path);
        valueList = valueList.getNode(valueList.getName());
        NodeIterator it = valueList.getNodes();
        List<SiteSpecificResource.ListItem> items = new ArrayList<>();
        while (it.hasNext()) {
            items.add(item( it.nextNode()));
        }
        return items;
    }

    SiteSpecificResource.ListItem item(Node item) throws RepositoryException {
        SiteSpecificResource.ListItem listitem = new SiteSpecificResource.ListItem();
        listitem.setLabel(item.getProperty("selection:label").getString());
        listitem.setKey(item.getProperty("selection:key").getString());
        return listitem;
    }

    public static class ListItem {

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
