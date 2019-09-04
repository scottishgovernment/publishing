package scot.gov.migration;

import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.pojo.model.ContentNode;

import javax.jcr.Session;

/**
 * Code to update or create a ContentNode in the repo.
 */
public class DocumentUpdater {

    public String update(Session session, String site, String path, ContentNode contentNode) {
        DocumentManager documentManager = new WorkflowDocumentManagerImpl(session);
        WorkflowDocumentVariantImportTask importTask = new WorkflowDocumentVariantImportTask(documentManager);
        String location = String.format("/content/documents/%s/%s/%s", site, path, contentNode.getName());
        String updatedDocumentLocation = importTask.createOrUpdateDocumentFromVariantContentNode(
                contentNode,
                contentNode.getPrimaryType(),
                location,
                "en",
                contentNode.getName());
        documentManager.publishDocument(updatedDocumentLocation);
        return updatedDocumentLocation;
    }
}
