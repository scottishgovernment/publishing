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
        JAXRSInvoker invoker = new JAXRSInvoker();
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(new ObjectMapper());
        return new CXFRepositoryJaxrsEndpoint(path)
                .invoker(invoker)
                .singleton(resource)
                .singleton(jacksonJsonProvider);
    }

    @Override
    protected void doShutdown() {
        RepositoryJaxrsService.removeEndpoint(TOPICS_PATH);
        RepositoryJaxrsService.removeEndpoint(PUBLICATION_TYPES_PATH);
    }

}
