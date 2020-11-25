package scot.gov.migration;

import org.onehippo.forge.content.exim.core.ContentMigrationException;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Rest resource to allow migrations to publish content.
 */
public class MigrationResource {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationResource.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

    Session daemonSession;

    MigrationUserCredentialsSource credentialsSource;

    DocumentUpdater documentUpdater = new DocumentUpdater();

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
            @QueryParam("publish") @DefaultValue("true") boolean publish,
            @QueryParam("createdBy") String createdBy,
            @QueryParam("lastModifiedBy") String lastModifiedBy,
            @QueryParam("lastModified") String lastModified,
            @QueryParam("created") String created,
            @HeaderParam("Authorization") String authHeader) {
        LOG.info("newPublishingDocument {}, {}, {}", contentNode.getName(), site, path);

        Session session = null;
        try {
            session = session(authHeader);
            String location = documentUpdater.update(
                    session,
                    site,
                    path,
                    publish,
                    contentNode,
                    createdBy,
                    lastModifiedBy,
                    getCalendar(created),
                    getCalendar(lastModified));
            return new Result(location);
        } catch (ContentMigrationException e) {
            LOG.error("Failed to create items", e);
            throw new WebApplicationException("Client error", 400);
        } catch (RepositoryException e) {
            LOG.error("Invalid item", e);
            throw new WebApplicationException("Servver error", 500);
        } catch (ParseException e) {
            LOG.error("Invalid date time", e);
            throw new WebApplicationException("Client error", 400);
        } finally  {
            logoutSafely(session);
        }
    }

    Calendar getCalendar(String str) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateFormat.parse(str));
        return cal;
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

        session.logout();
    }

    /**
     * Class to represent the result.
     */
    static class Result {

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
