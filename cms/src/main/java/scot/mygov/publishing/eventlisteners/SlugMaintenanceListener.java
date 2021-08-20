package scot.mygov.publishing.eventlisteners;

import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.*;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;
import static scot.mygov.publishing.eventlisteners.SlugLookups.SLUG;

/**
 * Maintain the data structure used by PublishingPlatformLinkProcessor to lookup slugs.
 *
 * We maintain 2 trees of slugs: one for preview and one for live.  Each one is a tree of slugs broken down by
 * letter where the leaf node maps to the content path for that item.  This allows the link processor to get the
 * right path for a slug with a simple session.getNode call rather than habing to do a query.
 */
public class SlugMaintenanceListener {

    private static final Logger LOG = LoggerFactory.getLogger(SlugMaintenanceListener.class);

    Session session;

    SlugLookups slugLookups;

    HippoUtils hippoUtils = new HippoUtils();

    public SlugMaintenanceListener(Session session) {
        this.session = session;
        this.slugLookups = new SlugLookups(session);
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {
        try {
            doHandleEvent(event);
        } catch (RepositoryException e) {
            ensureRefreshFalse(session);
            LOG.error(
                    "error trying to maintain slug event msg={}, action={}, event={}, result={}", e.getMessage(),
                    event.action(), event.category(), event.result(), e);
        }
    }

    void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        if (isFolderRename(event)) {
            updateLookupsInFolder(event);
            return;
        }

        if (!session.nodeExists(event.subjectPath())) {
            return;
        }

        Node subject = session.getNode(event.subjectPath());
        if (!requiresSlug(subject)) {
            return;
        }

        switch (event.action()) {
            case "commitEditableInstance":
                slugLookups.updateLookup(subject, "preview");
            break;

            case "publish" :
                slugLookups.updateLookup(subject, "live");
            break;

            case "depublish":
                slugLookups.removeLookup(subject, "live");
            break;

            case "delete":
                slugLookups.removeLookup(subject, "preview");
            break;

            default:
        }
    }

    boolean isFolderRename(HippoWorkflowEvent event) throws RepositoryException {
        if (!"rename".equals(event.action())) {
            return false;
        }

        return event.interaction().equals("embedded:folder-extended:rename");
    }

    void updateLookupsInFolder(HippoWorkflowEvent event) throws RepositoryException {
        String sitename = event.subjectPath().split("/")[3];
        String relpath = substringAfter(event.subjectPath(), sitename);
        String oldName = event.arguments().get(0).toString();
        String newName = event.arguments().get(1).toString();
        String xpath = String.format(
                "/jcr:root/content/urls/%s//element(*, nt:unstructured)[jcr:like(@publishing:path, '%s/%s/%%')]",
                sitename,
                relpath,
                oldName);
        hippoUtils.executeXpathQuery(session, xpath, node -> fixPublishingPath(node, relpath, oldName, newName));
        session.save();
    }

    void fixPublishingPath(Node node, String relpath, String oldName, String newName) throws RepositoryException {
        String path = node.getProperty("publishing:path").getString();
        String newPath = path.replace(relpath + "/" + oldName, relpath + "/" + newName);
        node.setProperty("publishing:path", newPath);
    }

    boolean requiresSlug(Node subject) throws RepositoryException {
        if (!subject.hasNode(subject.getName())) {
            return false;
        }

        Node variant = subject.getNode(subject.getName());
        return variant.hasProperty(SLUG);
    }


}
