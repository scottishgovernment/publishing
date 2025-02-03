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

import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;
import static scot.mygov.publishing.eventlisteners.MirrorEventListener.PUBLICATION_INTERACTION;

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
        if (!isSuccessfulPublish(event)) {
            return;
        }

        HippoNode handle = (HippoNode) session.getNodeByIdentifier(event.subjectId());
        Node published = hippoUtils.getPublishedOrDraftVariant(handle);
        if (published != null && published.isNodeType("publishing:Publication")) {
            updatePublicationTypes(published);
        }
    }

    boolean isSuccessfulPublish(HippoWorkflowEvent event) {
        return event.success() && PUBLICATION_INTERACTION.equals(event.interaction());
    }

    private void updatePublicationTypes(Node published) throws RepositoryException {
        Node site = (Node) published.getAncestor(3);
        Node publicationTypes = site.hasNode(PUBLICATION_TYPES)
                ? site.getNode(PUBLICATION_TYPES)
                : site.addNode(PUBLICATION_TYPES, "nt:unstructured");
        publicationTypes.setProperty(published.getProperty("publishing:publicationType").getString(), "true");
        session.save();
    }

}
