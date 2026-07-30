package scot.mygov.publishing.eventlisteners;

import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;

/**
 * The 'hippostd:hasfolders' property is usually set by the default folder workflow implementation.
 * This property is used to improve UI performance in brXM. The default implementation however, causes
 * issues with nested folders created via template queries.
 *
 * This results in our main content folders mistakenly having the property set to false, causing them
 * to appear like their folders cannot be expanded or collapsed in the CMS.
 *
 * This event listener sets this property where appropriate when folders are created in the CMS.
 */

public class HasFoldersImprovementListener {

    private static final String FOLDER_WORKFLOW_CLASS_NAME = "org.hippoecm.repository.standardworkflow.FolderWorkflowImpl";

    private static final Logger LOG = LoggerFactory.getLogger(HasFoldersImprovementListener.class);

    Session session;

    public HasFoldersImprovementListener(Session session) {
        this.session = session;
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {
        if (!canHandleEvent(event)) {
            return;
        }

        try {
            doHandleEvent(event);
        } catch (RepositoryException e) {
            ensureRefreshFalse(session);
            LOG.error(
                    "error trying to set hippostd:hasfolders property for event msg={}, action={}, event={}, result={}",
                    e.getMessage(), event.action(), event.category(), event.result(), e);
        }
    }

    boolean canHandleEvent(HippoWorkflowEvent event) {
        return event.success()
                && "add".equals(event.action())
                && FOLDER_WORKFLOW_CLASS_NAME.equals(event.className());
    }

    void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        final String result = event.result();
        if (result == null || !session.nodeExists(result)) {
            return;
        }

        final Node node = session.getNode(result);
        if (hasFolders(node)) {
            final String propertyName = "hippostd:hasfolders";
            if (!JcrUtils.getBooleanProperty(node, propertyName, false)) {
                node.setProperty(propertyName, true);
                session.save();
            }
        }
    }

    private boolean hasFolders(final Node node) throws RepositoryException {
        final NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            final Node next = nodes.nextNode();
            if (next.isNodeType("hippostd:folder")) {
                return true;
            }
        }
        return false;
    }

}