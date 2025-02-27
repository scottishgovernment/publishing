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

import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;

/**
 * When a Mirror is published we want to update its name and title to make it clear what the mirror points to.
 */
public class MirrorEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(MirrorEventListener.class);

    static final String PUBLISH_INTERACTION = "default:handle:publish";

    static final String DEPUBLISH_INTERACTION = "default:handle:depublish";

    Session session;

    HippoUtils hippoUtils = new HippoUtils();

    MirrorEventListener(Session session) {
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
        if (!isSuccessfulPublish(event)) {
            return;
        }

        HippoNode handle = (HippoNode) session.getNodeByIdentifier(event.subjectId());
        Node published = hippoUtils.getPublishedOrDraftVariant(handle);
        if (published != null && published.isNodeType("publishing:mirror")) {
            updateMirrorName(published);
        }
    }

    boolean isSuccessfulPublish(HippoWorkflowEvent event) {
        return event.success() && PUBLISH_INTERACTION.equals(event.interaction());
    }

    private void updateMirrorName(Node publishedNode) throws RepositoryException {
        String title = getMirroredTitle(publishedNode);
        String newName = String.format("Mirror: '%s'", title);
        Node handle = publishedNode.getParent();
        hippoUtils.ensureHasMixin(handle, "hippo:named");
        handle.setProperty("hippo:name", newName);
        hippoUtils.apply(handle.getNodes(), node -> node.setProperty("publishing:title", title));
        LOG.info("mirror name for {} is now {}", publishedNode.getPath(), newName);
        session.save();
    }

    String getMirroredTitle(Node mirror) throws RepositoryException {

        String docbase = mirror.getNode("publishing:document").getProperty("hippo:docbase").getString();

        // during the migration we set mirror docbases to cafebabe-cafe-babe-cafe-babecafebabe (the content root)
        // becuase we do not know if the mirrored item has been added yet.
        // after the migrastion we can remove this condition.
        if ("cafebabe-cafe-babe-cafe-babecafebabe".equals(docbase)) {
            return "no title available, migration still running";

        }
        Node mirroredHandle = session.getNodeByIdentifier(docbase);
        Node draft = hippoUtils.getVariantWithState(mirroredHandle, HippoStdNodeType.DRAFT);
        return draft.getProperty("publishing:title").getString();
    }

}
