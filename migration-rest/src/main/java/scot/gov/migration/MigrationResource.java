package scot.gov.migration;

import org.onehippo.forge.content.exim.core.DocumentManager;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentManagerImpl;
import org.onehippo.forge.content.exim.core.impl.WorkflowDocumentVariantImportTask;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Rest resource to allow migrations to publish content.
 */
public class MigrationResource {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationResource.class);

    Session daemonSession;

    MigrationUserCredentialsSource credentialsSource;

    public MigrationResource(Session daemonSession, MigrationUserCredentialsSource credentialsSource) {
        this.daemonSession = daemonSession;
        this.credentialsSource = credentialsSource;
    }

    /**
     * Publish a ContentNode for a given site and path.
     */
    @POST
    @Path("/{site}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Result newPublishingDocument(
            ContentNode contentNode,
            @PathParam("site") String site,
            @QueryParam("path") String path,
            @HeaderParam("Authorization") String authHeader) {
        LOG.info("newPublishingDocument {}, {}, {}", contentNode.getName(), site, path);

        Session session = null;
        try {
            session = session(authHeader);
            return doPost(session, site, path, contentNode);
        } catch (ConstraintViolationException e) {
            LOG.error("Failed to create items", e);
            throw new WebApplicationException("Server error", 400);
        } catch (RepositoryException e) {
            LOG.error("Invalid item", e);
            throw new WebApplicationException("Client error", 500);
        } finally  {
            logoutSafely(session);
        }
    }

    private Result doPost(Session session, String site, String path, ContentNode contentNode) {
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
        return new Result(updatedDocumentLocation);
    }

    /**
     * Impersonate the session used to initiate this module but use the redentials of the user who is posting
     * the content.
     */
    private Session session(String authHeader) throws RepositoryException {
        Credentials credentials = credentialsSource.get(authHeader);
        return daemonSession.impersonate(credentials);
    }

    /**
     * Best effort to ensure that the session is closed.
     ***/
    private void logoutSafely(Session session) {
        if (session == null) {
            return;
        }

        try {
            session.logout();
        } catch (Exception e) {
            LOG.error("Failed to logout JCR session.", e);
            throw new WebApplicationException("Server error", 500);
        }
    }

    /**
     * Class to represent the result.
     */
    class Result {

        String path;

        public Result(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
