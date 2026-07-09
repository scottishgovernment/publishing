package scot.gov.topics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import org.onehippo.repository.jaxrs.CXFRepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.jaxrs.api.ManagedUserSessionInvoker;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class Module extends AbstractReconfigurableDaemonModule {

    private static final String TOPICS_PATH = "/topics";

    private static final String PUBLICATION_TYPES_PATH = "/publicationtypes";


    @Override
    protected void doConfigure(Node module) throws RepositoryException {
        // nothing required
    }

    @Override
    protected void doInitialize(Session var1) throws RepositoryException {
        SiteSpecificResource topicsresource = new SiteSpecificResource(session, "topics");
        SiteSpecificResource publicationtypesresource = new SiteSpecificResource(session, "publicationtypes");
        RepositoryJaxrsService.addEndpoint(endpoint(topicsresource, TOPICS_PATH));
        RepositoryJaxrsService.addEndpoint(endpoint(publicationtypesresource,PUBLICATION_TYPES_PATH));
    }

    RepositoryJaxrsEndpoint endpoint(Object resource, String path) {
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(new ObjectMapper());
        // ManagedUserSessionInvoker authenticates via the caller's existing CMS HTTP session
        // (set as a request attribute by the CMS session filter) rather than HTTP Basic Auth.
        // Unauthenticated requests are rejected with 403. This lets the browser-based UI
        // extension call this endpoint without needing to supply credentials explicitly.
        return new CXFRepositoryJaxrsEndpoint(path)
                .invoker(new ManagedUserSessionInvoker(session))
                .singleton(resource)
                .singleton(jacksonJsonProvider);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(TOPICS_PATH);
        RepositoryJaxrsService.removeEndpoint(PUBLICATION_TYPES_PATH);
    }

}
