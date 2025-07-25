package scot.mygov.publishing.eventlisteners;

import org.apache.jackrabbit.commons.JcrUtils;
import org.hippoecm.repository.HippoStdNodeType;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.*;

import java.util.ArrayList;
import java.util.List;

import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;

/**
 * Maintain a list of the publication types used in  given site.
 */
public class ServiceFinderOrderListener {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceFinderOrderListener.class);

    private static final String URL = "publishing:url";

    private static final String LINKS = "publishing:links";

    private static final String LINK = "publishing:link";

    Session session;

    HippoUtils hippoUtils = new HippoUtils();

    ServiceFinderOrderListener(Session session) {
        this.session = session;
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {

        try {
            if (!"commitEditableInstance".equals(event.action())) {
                return;
            }

            doHandleEvent(event);
        } catch (RepositoryException e) {
            ensureRefreshFalse(session);
            LOG.error(
                    "error trying to sort service finder for event msg={}, action={}, event={}, result={}",
                    e.getMessage(), event.action(), event.category(), event.result(), e);
        }
    }

    void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        Node handle = session.getNodeByIdentifier(event.subjectId());
        hippoUtils.apply(handle.getNodes(handle.getName()), this::isUnpublished, variant ->
                hippoUtils.apply(variant.getNodes("publishing:contentBlocks"),
                        this::isSortableServiceFinder, this::sortLinks));
        session.save();
    }

    boolean isUnpublished(Node node) throws RepositoryException {
        return "unpublished".equals(JcrUtils.getStringProperty(node, HippoStdNodeType.HIPPOSTD_STATE, ""));
    }

    boolean isSortableServiceFinder(Node node) throws RepositoryException {
        return isServiceFinder(node) && JcrUtils.getBooleanProperty(node, "publishing:sorted", false);
    }

    boolean isServiceFinder(Node node) throws RepositoryException {
        return node.isNodeType("publishing:cb_externalservicefinder")
                || node.isNodeType("publishing:cb_internalservicefinder")
                || node.isNodeType("publishing:cb_councilservicefinder");
    }

    public void sortLinks(Node parent) throws RepositoryException {
        List<Link> links = new ArrayList<>();
        hippoUtils.apply(parent.getNodes(LINKS), n -> {
            links.add(new Link(n));
            n.remove();
        });
        links.sort(this::compare);
        for (Link link : links) {
            addNode(parent, link);
        }
    }

    void addNode(Node parent, Link link) throws RepositoryException {
        Node newNode = parent.addNode(LINKS, link.type);
        addMixins(newNode, "hippo:container", "hippostd:container", "hippostd:relaxed");
        newNode.setProperty("publishing:label", link.label);
        if (link.description != null) {
            newNode.setProperty("publishing:description", link.description);
        }
        if (link.url != null) {
            newNode.setProperty(URL, link.url);
        }
        if (link.docbase != null) {
            Node mirror = newNode.addNode(LINK, "hippo:mirror");
            mirror.setProperty("hippo:docbase", link.docbase);
        }
    }

    void addMixins(Node node, String ... mixins) throws RepositoryException {
        for (String mixin : mixins) {
            node.addMixin(mixin);
        }
    }

    static class Link {
        String type;
        String url;
        String description;
        String label;
        String docbase;

        public Link(Node link) throws RepositoryException {
            this.type = link.getPrimaryNodeType().getName();
            this.description = JcrUtils.getStringProperty(link,"publishing:description", null);
            this.label = link.getProperty("publishing:label").getString();
            this.url = JcrUtils.getStringProperty(link, URL, null);
            this.docbase = link.hasNode(LINK)
                    ? link.getNode(LINK).getProperty("hippo:docbase").getString() : null;
        }
    }

    private int compare(Link left, Link right) {
        return left.label.compareToIgnoreCase(right.label);
    }

}
