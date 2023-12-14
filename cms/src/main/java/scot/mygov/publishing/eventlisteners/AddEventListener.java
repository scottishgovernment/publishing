package scot.mygov.publishing.eventlisteners;

import org.onehippo.cms7.event.HippoEvent;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.contentbean.ValueListItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.*;
import java.util.*;

import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;
import static scot.mygov.publishing.eventlisteners.SlugMaintenanceListener.SLUG;

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

    private static final String NEW_FORMBASE = "new-publishing-formbase";

    private static final String NEW_FAIRRENT = "new-publishing-fairrent";

    private static final String NEW_SMARTANSWER = "new-publishing-smartanswer";

    private static final String NEW_STEPBYSTEP = "new-publishing-stepbystepguide";

    private static final String NEW_DOCUMENT_COVER_PAGE = "new-publishing-documentcoverpage";

    private static final String NEW_DOCUMENTS_FOLDER = "new-publishing-documents-folder";

    private static final String NEW_FRAGMENTS_FOLDER = "new-fragment-folder";

    protected HippoUtils hippoUtils;

    Set<String> orgFormats = new HashSet<>();

    Session session;

    AddEventListener(Session session, HippoUtils hippoUtils) {

        this.session = session;
        this.hippoUtils = hippoUtils;
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

        if (isGuideOrSmartAnswer(node)) {
            Node index = node.getNode(INDEX).getNode(INDEX);
            setSlug(index, node.getName());
            addRequiredOrganisations(index);
            session.save();
            return;
        }

        if (requiresSlug(node)) {
            setSlug(node, node.getName());
            if (hasOrganisationsTags(node)) {
                addRequiredOrganisations(node);
            }
            session.save();
            return;
        }

        if (isCategory(node)) {
            setActionsDependingOnDepth(node);
            setNavigationStyle(node);
            Node category = node.getNode(INDEX).getNode(INDEX);
            addRequiredOrganisations(category);
            session.save();
            return;
        }

        if (hasOrganisationsTags(node)) {
            addRequiredOrganisations(node);
            session.save();
            return;
        }

    }

    boolean isGuideOrSmartAnswer(Node node) throws RepositoryException {
        return isFolder(node)
                && isUnder(node, "/content/documents/")
                && (hasFolderAction(node, "new-publishing-guide-page")
                    || hasFolderAction(node, "new-publishing-smartanswer-result"));
    }

    boolean hasOrganisationsTags(Node node) throws RepositoryException {
        return node.hasProperty("publishing:organisationtags");
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
        // combine with existing actions
        folder.setProperty("hippostd:foldertype", folderTypes);
    }

    String [] allActions() {
        return new String[] { NEW_ARTICLE, NEW_CATEGORY, NEW_GUIDE, NEW_MIRROR, NEW_FORMBASE, NEW_FAIRRENT, NEW_SMARTANSWER, NEW_STEPBYSTEP, NEW_DOCUMENT_COVER_PAGE, NEW_DOCUMENTS_FOLDER, NEW_FRAGMENTS_FOLDER };
    }

    String [] actionsWithoutNewCategory() {
        return new String[] { NEW_ARTICLE, NEW_GUIDE, NEW_MIRROR, NEW_FORMBASE, NEW_FAIRRENT, NEW_SMARTANSWER, NEW_STEPBYSTEP, NEW_DOCUMENT_COVER_PAGE, NEW_DOCUMENTS_FOLDER, NEW_FRAGMENTS_FOLDER };
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

    boolean requiresSlug(Node node) throws RepositoryException {
        return node.hasProperty(SLUG);
    }

    void addRequiredOrganisations(Node node) throws RepositoryException {
        // Build a list of groups for the user who created the content document
        // that have a corresponding organisation tag for per-attribute permissions
        Node groups = session.getNode("/hippo:configuration/hippo:groups/");
        List<String> orgGroups = new ArrayList<>();
        NodeIterator groupsNodes = groups.getNodes();
        String username = node.getProperty("hippostdpubwf:createdBy").getValue().getString();
        while (groupsNodes.hasNext()) {
            Node  group = groupsNodes.nextNode();
            String groupName = group.getName();
            if (groupName.contains("orgs-")) {
                getUsersOrgGroups(username, groupName, orgGroups);
            }
        }

        // get and set a list of tags valid for the user creating the content document
        Set<String> applicableTags = new HashSet<>();
        ValueList orgs = hippoUtils.getValueList(session, "/content/documents/publishing/valuelists/organisationtags");
        List<ValueListItem> orgMapping = orgs.getItems();
        for (String group : orgGroups) {
            for (ValueListItem item : orgMapping) {
                if (item.getKey().equals(group)) {
                    applicableTags.add(item.getLabel());
                    break;
                }
            }
        }

        String[] set = new String[applicableTags.size()];
        set = applicableTags.toArray(set);
        node.setProperty("publishing:organisationtags", set);
    }


    void getUsersOrgGroups(String username, String groupName, List<String> groups) throws RepositoryException {
        Node group = session.getNode(String.format("/hippo:configuration/hippo:groups/%s/", groupName));
        if (group.hasProperty("hipposys:members")) {
            List<Value> values = Arrays.asList(group.getProperty("hipposys:members").getValues());
            for (Value value : values) {
                if (value.getString().equals(username)) {
                    groups.add(groupName);
                    break;
                }
            }
        }
    }

    boolean isUnder(Node node, String path) throws RepositoryException {
        return node.getPath().startsWith(path);
    }

    void setSlug(Node node, String slug) throws RepositoryException {
        node.setProperty(SLUG, slug);
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
