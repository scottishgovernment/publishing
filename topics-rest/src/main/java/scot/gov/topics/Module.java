package scot.gov.topics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.onehippo.repository.jaxrs.*;
import org.onehippo.repository.modules.AbstractReconfigurableDaemonModule;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class Module extends AbstractReconfigurableDaemonModule {

    private static final String PATH = "/topics";

    @Override
    protected void doConfigure(Node module) throws RepositoryException {
        // nothing required
    }

    @Override
    protected void doInitialize(Session var1) throws RepositoryException {
        Resource resource = new Resource(session);

        RepositoryJaxrsEndpoint endpoint = new CXFRepositoryJaxrsEndpoint(PATH)
                .invoker(new JAXRSInvoker())
                .singleton(resource)
                .singleton(new JacksonJsonProvider(new ObjectMapper()));
        RepositoryJaxrsService.addEndpoint(endpoint);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(PATH);
    }

}
