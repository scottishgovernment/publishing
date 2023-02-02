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

    private static final String PREVIEW = "preview";

    private static final String LIVE = "live";

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

        if (isFolderMove(event)) {
            updateLookupsInFolderForFolderMove(event);
            return;
        }

        if (isFolderCopy(event)) {
            updateLookupsInFolderForFolderCopy(event);
            return;
        }

        if (isFolderRename(event)) {
            updateLookupsInFolderForFolderRename(event);
            return;
        }

        if (!session.nodeExists(event.subjectPath())) {
            return;
        }

        Node subject = session.getNode(event.subjectPath());
        if (!requiresSlug(subject)) {
            return;
        }

        handleEventByAction(event, subject);
    }


    void handleEventByAction(HippoWorkflowEvent event, Node subject) throws RepositoryException {
        switch (event.action()) {
            case "commitEditableInstance":
                slugLookups.updateLookup(subject, PREVIEW);
                break;

            case "publish" :
                slugLookups.updateLookup(subject, LIVE);
                break;

            case "depublish":
                slugLookups.removeLookup(subject, LIVE);
                break;

            case "delete":
                slugLookups.removeLookup(subject, PREVIEW);
                break;

            default:
        }
    }

    boolean isFolderRename(HippoWorkflowEvent event) {
        if (!"rename".equals(event.action())) {
            return false;
        }

        return "embedded:folder-extended:rename".equals(event.interaction());
    }

    boolean isFolderMove(HippoWorkflowEvent event) {
        if (!"moveFolder".equals(event.action())) {
            return false;
        }

        return "threepane:folder-permissions:moveFolder".equals(event.interaction());
    }

    boolean isFolderCopy(HippoWorkflowEvent event) {
        if (!"copyFolder".equals(event.action())) {
            return false;
        }

        return "threepane:folder-permissions:copyFolder".equals(event.interaction());
    }

    void updateLookupsInFolderForFolderRename(HippoWorkflowEvent event) throws RepositoryException {
        String sitename = getSitenameFromSubjectPath(event.subjectPath());
        String fromPath = substringAfter(event.subjectPath(), sitename) + "/" + event.arguments().get(0).toString();
        String toPath =   substringAfter(event.subjectPath(), sitename) + "/" + event.arguments().get(1).toString();
        updateUrlLookupsForFolderMove(sitename, fromPath, toPath);
        session.save();
    }

    void updateLookupsInFolderForFolderMove(HippoWorkflowEvent event) throws RepositoryException {

        String sitename = getSitenameFromSubjectPath(event.subjectPath());
        String fromPath = substringAfter(event.subjectPath(), sitename);
        String toFolder = session.getNodeByIdentifier(event.arguments().get(2).toString()).getPath();
        String toPath = substringAfter(toFolder, sitename) + "/" + event.arguments().get(3);
        updateUrlLookupsForFolderMove(sitename, fromPath, toPath);
        session.save();
    }

    void updateUrlLookupsForFolderMove(String sitename, String fromPath, String toPath) throws RepositoryException{
        String xpath = String.format(
                "/jcr:root/content/urls/%s//element(*, nt:unstructured)[jcr:like(@publishing:path, '%s/%%')]",
                sitename,
                fromPath);
        LOG.info("updateUrlLookupsForFolderMove {}", xpath);
        hippoUtils.executeXpathQuery(session, xpath, node -> {
            String oldPath = node.getProperty("publishing:path").getString();
            String newPath = oldPath.replace(fromPath, toPath);
            LOG.info("setting {} -> {}", oldPath, newPath);
            node.setProperty("publishing:path", newPath);
        });
    }

    void updateLookupsInFolderForFolderCopy(HippoWorkflowEvent event) throws RepositoryException {
        String sitename = getSitenameFromSubjectPath(event.subjectPath());
        String toFolder = session.getNodeByIdentifier(event.arguments().get(2).toString()).getPath();
        String toPath = substringAfter(toFolder, sitename) + "/" + event.arguments().get(3);
        String xpath = String.format(
                "/jcr:root/content/documents/%s%s//*[publishing:slug != '']",
                sitename,
                toPath);

        hippoUtils.executeXpathQuery(session, xpath, node -> {
            String slug = node.getProperty(SLUG).getString();
            String newSlug = slug + "-copy";
            node.setProperty(SLUG, newSlug);
            session.save();
            slugLookups.updateLookup(node.getParent(), PREVIEW);
            LOG.info("updateLookupsInFolderForFolderCopy {}: {} -> {}", node.getPath());
        });
    }

    String getSitenameFromSubjectPath(String path) {
        return path.split("/")[3];
    }

    boolean requiresSlug(Node subject) throws RepositoryException {
        if (!subject.hasNode(subject.getName())) {
            return false;
        }

        Node variant = subject.getNode(subject.getName());
        return variant.hasProperty(SLUG);
    }


}
