package scot.mygov.publishing.eventlisteners;

import org.onehippo.cms7.event.HippoEvent;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Listen for new category pages / folders being created in order to:
 *  - limit the depth of the nested categories to MAX_LEVELS
 *  - set the default value for the navigation style according to the depth
 */
public class AddCategoryEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(AddCategoryEventListener.class);

    // the maximum nesting for categories
    protected static final int MAX_LEVELS = 10;

    private static final String NEW_ARTICLE = "new-publishing-article";
    private static final String NEW_CATEGORY = "new-publishing-category";

    Session session;

    AddCategoryEventListener(Session session) {
        this.session = session;
    }

    @Subscribe
    public void handleEvent(HippoEvent event) {
        try {
            doHandleEvent(event);
        } catch (RepositoryException e) {
            LOG.error(
                    "error trying to set folder actions for event action={}, event={}, result={}",
                    event.action(), event.category(), event.result());
        }
    }

    void doHandleEvent(HippoEvent event) throws RepositoryException {
        Node node = session.getNode(event.result());
        if (isFolderAddEvent(event, node)) {
            setActionsDependingOnDepth(node);
            setNavigationStyle(node);
            session.save();
        }
    }

    void setActionsDependingOnDepth(Node folder) throws RepositoryException {
        String [] folderTypes = canCreateChildCategories(folder)
                                ? allActions()
                                : actionsWithoutNewCategory();
        folder.setProperty("hippostd:foldertype", folderTypes);
    }

    String [] allActions() {
        return new String[] { NEW_ARTICLE, NEW_CATEGORY };
    }

    String [] actionsWithoutNewCategory() {
        return new String[] { NEW_ARTICLE };
    }

    void setNavigationStyle(Node folder) throws RepositoryException {
        String navigationType  = navigationStyleForDepth(folder);
        NodeIterator it = folder.getNode("index").getNodes();
        while (it.hasNext()) {
            Node index = it.nextNode();
            index.setProperty("publishing:navigationType", navigationType);
        }
    }

    boolean isFolderAddEvent(HippoEvent event, Node node) throws RepositoryException {
        return isAddEvent(event) && isFolder(node);
    }

    boolean isAddEvent(HippoEvent event) {
        return "add".equals(event.action());
    }

    boolean isFolder(Node node) throws RepositoryException {
        return node.isNodeType("hippostd:folder");
    }

    boolean canCreateChildCategories(Node folder) throws RepositoryException {
        return categoryDepth(folder) < MAX_LEVELS;
    }

    String navigationStyleForDepth(Node folder) throws RepositoryException {
        // we default the first level to be grid navigation alnd all other levels to be lists
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
