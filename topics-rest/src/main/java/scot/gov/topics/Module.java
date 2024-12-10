package scot.gov.topics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.onehippo.repository.jaxrs.RepositoryJaxrsEndpoint;
import org.onehippo.repository.jaxrs.RepositoryJaxrsService;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;


public class Module extends AbstractReconfigurableDaemonModule {

    private static final String PATH = "/internal/topics";

    @Override
    protected void doConfigure(Node module) throws RepositoryException {
        // no configuration needed
    }

    @Override
    protected void doInitialize(Session var1) throws RepositoryException {
        Resource resource = new Resource(session);
        RepositoryJaxrsEndpoint endpoint = new RepositoryJaxrsEndpoint(PATH)
                .singleton(resource)
                .singleton(new JacksonJsonProvider(new ObjectMapper()));
        RepositoryJaxrsService.addEndpoint(endpoint);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(PATH);
    }

}
