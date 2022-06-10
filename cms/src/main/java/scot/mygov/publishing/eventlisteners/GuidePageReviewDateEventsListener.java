package scot.mygov.publishing.eventlisteners;

import org.hippoecm.repository.api.HippoNode;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.*;

import java.util.Calendar;
import java.util.Objects;

import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;

/**
 * When a guide is edited, update the guide pages so that they have the same review date.
 */
public class GuidePageReviewDateEventsListener {

    private static final Logger LOG = LoggerFactory.getLogger(MirrorEventListener.class);

    private static final String REVIEW_DATE = "publishing:reviewDate";

    Session session;

    HippoUtils hippoUtils = new HippoUtils();

    GuidePageReviewDateEventsListener(Session session) {
        this.session = session;
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {
        try {
            doHandleEvent(event);
        } catch (RepositoryException e) {
            ensureRefreshFalse(session);
            LOG.error(
                    "error trying to update guide page life events for event msg={}, action={}, event={}, result={}",
                    e.getMessage(), event.action(), event.category(), event.result(), e);
        }
    }

    void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        if (!isGuideEdit(event)) {
            return;
        }

        HippoNode handle = (HippoNode) session.getNodeByIdentifier(event.subjectId());
        Node guide = hippoUtils.getDraftVariant(handle);
        Node folder = handle.getParent();
        Calendar reviewDate = getReviewDate(guide);
        if (peformUpdates(folder, reviewDate)) {
            session.save();
        }
    }

    Calendar getReviewDate(Node node) throws RepositoryException {
        return node.hasProperty(REVIEW_DATE) ? node.getProperty(REVIEW_DATE).getDate() : null;
    }

    boolean peformUpdates(Node folder, Calendar reviewDate) throws RepositoryException {
        boolean changes = false;
        NodeIterator it = folder.getNodes();
        while (it.hasNext()) {
            Node guidepagehandle = it.nextNode();
            if (isGuidePage(guidepagehandle)) {
                changes = updateIfChanged(guidepagehandle, reviewDate);
            }
        }
        return changes;
    }

    boolean updateIfChanged(Node node, Calendar guideReviewDate) throws RepositoryException{
        boolean changes = false;

        NodeIterator it = node.getNodes(node.getName());
        while (it.hasNext()) {
            Node guidepage = it.nextNode();
            Calendar guidepageReviewDate = getReviewDate(guidepage);
            if (!Objects.equals(guideReviewDate, guidepageReviewDate)) {
                guidepage.setProperty(REVIEW_DATE, guideReviewDate);
                changes = true;
            }
        }
        return changes;
    }

    boolean isGuidePage(Node node) throws RepositoryException {
        return node.hasNode(node.getName()) && node.getNode(node.getName()).isNodeType("publishing:guidepage");
    }

    boolean isGuideEdit(HippoWorkflowEvent event) {
        return "publishing:guide".equals(event.documentType())
                &&"commitEditableInstance".equals(event.action());
    }
}