package scot.mygov.publishing.eventlisteners;

import org.onehippo.cms7.event.HippoEvent;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;

/**
 * Listen for new pages / folders being created in order to:
 *  - copy the jcr:name into the slug field
 *  - limit the depth of the nested categories to MAX_LEVELS
 *  - set the default value for the navigation style according to the depth
 */
public class AddEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(AddEventListener.class);

    // the maximum nesting for categories
    protected static final int MAX_LEVELS = 10;

    private static final String INDEX = "index";

    private static final String NEW_ARTICLE = "new-publishing-article";

    private static final String NEW_CATEGORY = "new-publishing-category";

    private static final String NEW_GUIDE = "new-publishing-guide";

    private static final String NEW_MIRROR = "new-publishing-mirror";

    Set<String> orgFormats = new HashSet<>();

    Session session;

    HippoUtils hippoUtils = new HippoUtils();

    AddEventListener(Session session) {

        this.session = session;
        Collections.addAll(orgFormats, "publishing:organisation", "publishing:organisationlist");
    }

    @Subscribe
    public void handleEvent(HippoEvent event) {
        try {
            doHandleEvent(event);
        } catch (RepositoryException e) {
            ensureRefreshFalse(session);
            LOG.error(
                    "error trying to set folder actions for event msg={}, action={}, event={}, result={}", e.getMessage(),
                    event.action(), event.category(), event.result(), e);
        }
    }

    void doHandleEvent(HippoEvent event) throws RepositoryException {
        if (!isAddEvent(event)) {
            return;
        }

        Node node = session.getNode(event.result());

        // during the migration we will see the organisation list etc get created here.
        // we do not want to take any avtion for these.
        if (hippoUtils.isOneOfNodeTypes(node, "publishing:organisation", "publishing:organisationlist")) {
            return;
        }

        if (isArticle(node)) {
            node.setProperty("publishing:slug", node.getName());
            session.save();
            return;
        }

        if (isGuide(node)) {
            Node index = node.getNode(INDEX).getNode(INDEX);
            index.setProperty("publishing:slug", node.getName());
            session.save();
            return;
        }

        if (isCategory(node)) {
            setActionsDependingOnDepth(node);
            setNavigationStyle(node);
            session.save();
        }
    }

    boolean isGuide(Node node) throws RepositoryException {
        return isFolder(node)
                && isUnder(node, "/content/documents/")
                && hasFolderAction(node, "new-publishing-guide-page");
    }

    boolean isCategory(Node node) throws RepositoryException {
        return isFolder(node)
                && isUnder(node, "/content/documents/")
                && hasFolderAction(node, NEW_CATEGORY);
    }

    boolean hasFolderAction(Node node, String action) throws RepositoryException {
        Value[] values = node.getProperty("hippostd:foldertype").getValues();
        for (Value value : values) {
            if (value.getString().equals(action)) {
                return true;
            }
        }
        return false;
    }

    void setActionsDependingOnDepth(Node folder) throws RepositoryException {
        String [] folderTypes = canCreateChildCategories(folder)
                                ? allActions()
                                : actionsWithoutNewCategory();
        folder.setProperty("hippostd:foldertype", folderTypes);
    }

    String [] allActions() {
        return new String[] { NEW_ARTICLE, NEW_CATEGORY, NEW_GUIDE, NEW_MIRROR };
    }

    String [] actionsWithoutNewCategory() {
        return new String[] { NEW_ARTICLE, NEW_GUIDE, NEW_MIRROR };
    }

    void setNavigationStyle(Node folder) throws RepositoryException {
        String navigationType  = navigationStyleForDepth(folder);
        NodeIterator it = folder.getNode(INDEX).getNodes();
        while (it.hasNext()) {
            Node index = it.nextNode();
            index.setProperty("publishing:navigationType", navigationType);
        }
    }

    boolean isAddEvent(HippoEvent event) {
        return "add".equals(event.action());
    }

    boolean isFolder(Node node) throws RepositoryException {
        return node.isNodeType("hippostd:folder");
    }

    boolean isArticle(Node node) throws RepositoryException {
        return node.isNodeType("publishing:article");
    }

    boolean isUnder(Node node, String path) throws RepositoryException {
        return node.getPath().startsWith(path);
    }

    boolean canCreateChildCategories(Node folder) throws RepositoryException {
        return categoryDepth(folder) < MAX_LEVELS;
    }

    String navigationStyleForDepth(Node folder) throws RepositoryException {
        // we default the first level to be grid navigation and all other levels to be lists
        return categoryDepth(folder) == 1 ? "grid" : "list";
    }

    /**
     * Work out the depth of category
     *
     * - 3 is here because we want the depth relative to the site.
     * The path starts with: /content/documents/site-name
     */
    int categoryDepth(Node folder) throws RepositoryException {
        return folder.getDepth() -  3;
    }
}
