package scot.gov.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;
import static org.onehippo.repository.jaxrs.RepositoryJaxrsService.HIPPO_REST_PERMISSION;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * MigrationModule to register the MigrationResource used by migrations to create content.
 */
public class MigrationModule extends AbstractReconfigurableDaemonModule {

    // From the hippo docs here:
    // https://documentation.bloomreach.com/library/concepts/hippo-services/repository-jaxrs-service.html
    //
    // The repository authorization model is implemented such that checking for a permission on a path that does not
    // exist for any user always succeeds. In other words, it is highly recommended to use a path that is guaranteed
    // to exist for the authorization check. For Repository daemon modules, best practise is to use the modules
    // configuration root for this.
    //
    // this is across the whole site and so I have decied to make it the documents directory
    private static final String AUTH_PATH = "/content/documents";

    private static final String PATH = "/publishing/migration";

    @Override
    protected void doConfigure(Node module) throws RepositoryException {
        // no configuration needed
    }

    @Override
    protected void doInitialize(Session var1) throws RepositoryException {
        MigrationUserCredentialsSource credentialsSource = new MigrationUserCredentialsSource();
        MigrationResource migrationResource = new MigrationResource(session, credentialsSource);
        RepositoryJaxrsEndpoint endpoint = new RepositoryJaxrsEndpoint(PATH)
                .singleton(migrationResource)
                // the user authenticating must have the hippo:rest permisison on the root fo the documents folder.
                .authorized(AUTH_PATH, HIPPO_REST_PERMISSION)
                .singleton(new JacksonJsonProvider(new ObjectMapper()));
        RepositoryJaxrsService.addEndpoint(endpoint);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(PATH);
    }

}
