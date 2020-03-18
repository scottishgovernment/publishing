package scot.mygov.publishing.eventlisteners;

import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.api.HippoNode;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * When a Mirror is published we want to update its name to make it clear what the mirror points to.
 */
public class MirrorNameEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(MirrorNameEventListener.class);

    private static final String PUBLICATION_INTERACTION = "default:handle:publish";

    Session session;

    MirrorNameEventListener(Session session) {
        this.session = session;
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {
        try {
            doHandleEvent(event);
        } catch (RepositoryException e) {
            LOG.error(
                    "error trying to update mirror name for event msg={}, action={}, event={}, result={}",
                    e.getMessage(), event.action(), event.category(), event.result(), e);
        }
    }

    void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        if (!isSucessfulPublish(event)) {
            return;
        }

        HippoNode handle = (HippoNode) session.getNodeByIdentifier(event.subjectId());
        Node published = HippoUtils.getPublishedVariant(handle);
        if (published != null && published.isNodeType("publishing:mirror")) {
            updateMirrorName(published);
        }
    }

    boolean isSucessfulPublish(HippoWorkflowEvent event) {
        return event.success() && PUBLICATION_INTERACTION.equals(event.interaction());
    }

    private void updateMirrorName(Node publishedNode) throws RepositoryException {
        String title = getMirrorTitle(publishedNode);
        String newName = String.format("Mirror: '%s'", title);
        Node handle = publishedNode.getParent();
        HippoUtils.ensureHasMixin(handle, "hippo:named");
        handle.setProperty("hippo:name", newName);
        session.save();
    }

    String getMirrorTitle(Node mirror) throws RepositoryException {
        String documentId = mirror.getNode("publishing:document").getProperty("hippo:docbase").getString();
        Node mirroredHandle = session.getNodeByIdentifier(documentId);
        Node draft = HippoUtils.getVariantWithState(mirroredHandle, HippoStdNodeType.DRAFT);
        return draft.getProperty("publishing:title").getString();
    }

}
