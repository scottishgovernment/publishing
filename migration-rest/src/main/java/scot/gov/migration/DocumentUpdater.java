package scot.gov.migration;

import org.apache.commons.lang3.StringUtils;
import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Calendar;

/**
 * Code to update or create a ContentNode in the repo.
 */
public class DocumentUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationResource.class);

    public String update(
            Session session,
            String site,
            String path,
            boolean publish,
            ContentNode contentNode,
            String createdBy,
            String lastModifiedBy,
            Calendar created,
            Calendar lastModified) {
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

        try {
            if (updatedDocumentLocation.endsWith("[2]")) {
                session.removeItem(updatedDocumentLocation);
                updatedDocumentLocation = StringUtils.substringBeforeLast(updatedDocumentLocation, "[2]");
            }
            Node handle = session.getNode(updatedDocumentLocation);

            NodeIterator it = handle.getNodes();
            while (it.hasNext()) {
                Node variant = it.nextNode();
                variant.setProperty("hippostdpubwf:lastModifiedBy", lastModifiedBy);
                variant.setProperty("hippostdpubwf:createdBy", createdBy);
                variant.setProperty("hippostdpubwf:lastModificationDate", lastModified);
                variant.setProperty("hippostdpubwf:creationDate", created);
                if ("published".equals(variant.getProperty("hippostd:state").getString())) {
                    variant.setProperty("hippostdpubwf:publicationDate", lastModified);
                }
            }
            session.save();
        } catch (RepositoryException e) {
            LOG.error("Failed to update created by and updated by fields", e);
        }

        return updatedDocumentLocation;
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
