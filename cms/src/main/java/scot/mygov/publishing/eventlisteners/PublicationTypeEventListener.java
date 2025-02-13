package scot.mygov.publishing.eventlisteners;

import org.hippoecm.repository.api.HippoNode;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.ArrayList;
import java.util.List;

import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;
import static scot.mygov.publishing.eventlisteners.MirrorEventListener.DEPUBLISH_INTERACTION;
import static scot.mygov.publishing.eventlisteners.MirrorEventListener.PUBLISH_INTERACTION;

/**
 * Maintain a list of the publication types used in  given site.
 */
public class PublicationTypeEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationTypeEventListener.class);

    private static final String PUBLICATION_TYPES = "administration/publicationTypes";

    Session session;

    HippoUtils hippoUtils = new HippoUtils();

    PublicationTypeEventListener(Session session) {
        this.session = session;
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {
        try {
            doHandleEvent(event);
        } catch (RepositoryException e) {
            ensureRefreshFalse(session);
            LOG.error(
                    "error trying to update mirror name for event msg={}, action={}, event={}, result={}",
                    e.getMessage(), event.action(), event.category(), event.result(), e);
        }
    }

    void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        if (isPublish(event)) {
            handlePublish(event);
            return;
        }

        if (isDepublish(event)) {
            handleDepublish(event);
        }
    }

    boolean isPublish(HippoWorkflowEvent event) {
        return event.success() && PUBLISH_INTERACTION.equals(event.interaction());
    }

    boolean isDepublish(HippoWorkflowEvent event) {
        return event.success() && DEPUBLISH_INTERACTION.equals(event.interaction());
    }

    void handlePublish(HippoWorkflowEvent event) throws RepositoryException {
        HippoNode handle = (HippoNode) session.getNodeByIdentifier(event.subjectId());
        Node variant = hippoUtils.getPublishedOrDraftVariant(handle);
        if (variant != null && variant.isNodeType("publishing:Publication")) {
            updatePublicationTypesForPublish(variant);
        }
    }

    void handleDepublish(HippoWorkflowEvent event) throws RepositoryException {
        HippoNode handle = (HippoNode) session.getNodeByIdentifier(event.subjectId());
        Node variant = hippoUtils.getPublishedOrDraftVariant(handle);
        if (variant != null && variant.isNodeType("publishing:Publication")) {
            updatePublicationTypesForDeublish(variant);
        }
    }

    private void updatePublicationTypesForPublish(Node variant) throws RepositoryException {
        updateTypeUsed(variant, true);
    }

    private void updatePublicationTypesForDeublish(Node variant) throws RepositoryException {
        String type = publicationType(variant);
        Node site = site(variant);
        String xpath = publicationsOfTypeQuery(site.getIdentifier(), type);
        List<Node> nodes = new ArrayList<>();
        hippoUtils.executeXpathQuery(session, xpath, nodes::add);
        if (nodes.isEmpty()) {
            updateTypeUsed(variant, false);
        }
    }

    String publicationsOfTypeQuery(String siteId, String type) {
        // convert to the ancestors style
        return String.format("//element(*, publishing:Publication)" +
                "[hippo:paths = '%s']" +
                "[hippostd:stateSummary = 'live']" +
                "[hippostd:state = 'published']" +
                "[publishing:publicationType = '%s']",
                siteId,
                type);
    }

    void updateTypeUsed(Node node, boolean used) throws RepositoryException {
        Node typeNode = publicationTypeNode(node);
        String type = publicationType(node);
        if (used) {
            typeNode.setProperty(type, Boolean.toString(used));
        } else {
            typeNode.getProperty(type).remove();
        }
        session.save();
    }

    String publicationType(Node node) throws RepositoryException {
        return node.getProperty("publishing:publicationType").getString();
    }

    Node publicationTypeNode(Node node) throws RepositoryException {
        Node site = site(node);
        return site.hasNode(PUBLICATION_TYPES)
                ? site.getNode(PUBLICATION_TYPES)
                : site.addNode(PUBLICATION_TYPES, "nt:unstructured");
    }

    Node site(Node node) throws RepositoryException {
        return (Node) node.getAncestor(3);
    }
}
