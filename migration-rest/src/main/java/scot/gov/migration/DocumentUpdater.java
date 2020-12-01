package scot.gov.migration;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.pojo.model.ContentNode;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Code to update or create a ContentNode in the repo.
 */
public class DocumentUpdater {

    public String update(
            Session session,
            String site,
            String path,
            boolean publish,
            ContentNode contentNode,
            ContentAuthorship authorship) throws RepositoryException {
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        WorkflowDocumentVariantImportTask importTask = new WorkflowDocumentVariantImportTask(documentManager);
        String location = String.format("/content/documents/%s/%s/%s", site, path, contentNode.getName());
        String updatedDocumentLocation = importTask.createOrUpdateDocumentFromVariantContentNode(
                contentNode,
                contentNode.getPrimaryType(),
                location,
                "en",
                localizedName(contentNode));
        publish(documentManager, publish, updatedDocumentLocation);
        updatedDocumentLocation = removeAutoCreatedIndexIfPresent(session, updatedDocumentLocation);
        setAuthorship(session, updatedDocumentLocation, authorship);
        session.save();
        return updatedDocumentLocation;
    }

    String removeAutoCreatedIndexIfPresent(Session session, String location) throws RepositoryException {
        if (location.endsWith("[2]")) {
            session.removeItem(location);
            return StringUtils.substringBeforeLast(location, "[2]");
        } else {
            return location;
        }
    }

    void setAuthorship(Session session, String location, ContentAuthorship authorship) throws RepositoryException {

        Node handle = session.getNode(location);

        NodeIterator it = handle.getNodes();
        while (it.hasNext()) {
            Node variant = it.nextNode();
            variant.setProperty("hippostdpubwf:lastModifiedBy", authorship.getModifiedBy());
            variant.setProperty("hippostdpubwf:createdBy", authorship.getCreatedBy());
            variant.setProperty("hippostdpubwf:lastModificationDate", authorship.getModifiedDate());
            variant.setProperty("hippostdpubwf:creationDate", authorship.getCreatedDate());
            if ("published".equals(variant.getProperty("hippostd:state").getString())) {
                variant.setProperty("hippostdpubwf:publicationDate", authorship.getModifiedDate());
            }
        }
    }

    static void publish(DocumentManager documentManager, boolean publish, String location) {
        if (publish) {
            documentManager.publishDocument(location);
        }
    }

    static String localizedName(ContentNode contentNode) {
        return contentNode.hasProperty("hippo:name")
                ? contentNode.getProperty("hippo:name").getValue()
                : contentNode.getName();
    }
}
