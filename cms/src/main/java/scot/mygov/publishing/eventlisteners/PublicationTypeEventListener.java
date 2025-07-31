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
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;
import static scot.mygov.publishing.eventlisteners.MirrorEventListener.DEPUBLISH_INTERACTION;
import static scot.mygov.publishing.eventlisteners.MirrorEventListener.PUBLISH_INTERACTION;

/**
 * Maintain a list of the publication types used in  given site.
 */
public class PublicationTypeEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationTypeEventListener.class);

    private static final String PUBLICATION = "publishing:Publication";

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
        if (isPublish(event)) {
            handlePublish(event);
            return;
        }

        if (isDepublish(event)) {
            handleDepublish(event);
        }
    }

    boolean isPublish(HippoWorkflowEvent event) {
        return event.success() && PUBLISH_INTERACTION.equals(event.interaction());
    }

    boolean isDepublish(HippoWorkflowEvent event) {
        return event.success() && DEPUBLISH_INTERACTION.equals(event.interaction());
    }

    void handlePublish(HippoWorkflowEvent event) throws RepositoryException {
        Node handle = session.getNodeByIdentifier(event.subjectId());
        Node publishedVariant = hippoUtils.getPublishedOrDraftVariant(handle);
        if (!publishedVariant.isNodeType(PUBLICATION)) {
            return;
        }
        Node versionable = hippoUtils.findFirst(handle.getNodes(handle.getName()), v -> v.isNodeType("mix:versionable") );
        if (versionable != null) {
            VersionHistory versionHistory = session.getWorkspace().getVersionManager().getVersionHistory(versionable.getPath());
            VersionIterator versionIterator = versionHistory.getAllVersions();
            versionIterator.nextVersion();
            if (versionIterator.hasNext()) {
                Version lastVersion = versionIterator.nextVersion();
                updatePublicationTypesForDeublish(lastVersion.getFrozenNode(), site(handle));
            }
        }

        if (publishedVariant != null && publishedVariant.isNodeType(PUBLICATION)) {
            updatePublicationTypesForPublish(publishedVariant);
        }
    }

    void handleDepublish(HippoWorkflowEvent event) throws RepositoryException {
        HippoNode handle = (HippoNode) session.getNodeByIdentifier(event.subjectId());
        Node variant = hippoUtils.getPublishedOrDraftVariant(handle);
        if (variant != null && variant.isNodeType(PUBLICATION)) {
            updatePublicationTypesForDeublish(variant, site(variant));
        }
    }

    private void updatePublicationTypesForPublish(Node variant) throws RepositoryException {
        updateTypeUsed(variant, site(variant), true);
    }

    private void updatePublicationTypesForDeublish(Node variant, Node site) throws RepositoryException {
        String type = publicationType(variant);
        String xpath = publicationsOfTypeQuery(site.getIdentifier(), type);
        List<Node> nodes = new ArrayList<>();
        hippoUtils.executeXpathQuery(session, xpath, nodes::add);
        if (nodes.isEmpty()) {
            updateTypeUsed(variant, site, false);
        }
    }

    String publicationsOfTypeQuery(String siteId, String type) {
        // convert to the ancestors style
        return String.format("//element(*, publishing:Publication)" +
                        "[hippo:paths = '%s']" +
                        "[hippostd:stateSummary = 'live']" +
                        "[hippostd:state = 'published']" +
                        "[publishing:publicationType = '%s']",
                siteId,
                type);
    }

    void updateTypeUsed(Node node, Node site, boolean isUsed) throws RepositoryException {
        String type = publicationType(node);
        if (isEmpty(type)) {
            return;
        }

        Node publicationTypesNode = publicationTypesNode(site);
        if (isUsed) {
            publicationTypesNode.setProperty(type, Boolean.toString(isUsed));
            session.save();
            return;
        }

        if (publicationTypesNode.hasProperty(type)) {
            publicationTypesNode.getProperty(type).remove();
            session.save();
        }
    }

    String publicationType(Node node) throws RepositoryException {
        return node.getProperty("publishing:publicationType").getString();
    }

    Node publicationTypesNode(Node site) throws RepositoryException {
        Node adminFolder = session.getNode("/content/publicationtypes");
        return getOrCreate(adminFolder, site.getName());
    }

    Node getOrCreate(Node node, String name) throws RepositoryException {
        return node.hasNode(name) ? node.getNode(name) : node.addNode(name,"nt:unstructured");
    }

    Node site(Node node) throws RepositoryException {
        return (Node) node.getAncestor(3);
    }
}
