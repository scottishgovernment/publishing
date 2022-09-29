package scot.mygov.publishing.scheduler;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hippoecm.hst.core.container.ComponentManager;
import org.hippoecm.hst.core.container.ContainerConfiguration;
import org.hippoecm.hst.site.HstServices;
import org.hippoecm.repository.api.*;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Calendar;

public class PollFunnelbackCurator implements RepositoryJob {

    private static final Logger LOG = LoggerFactory.getLogger(PollFunnelbackCurator.class);

    private static final String HASH = "publishing:hash";

    private static final String CURATOR_PATH = "/content/documents/administration/funnelback-curator-changes";

    @Override
    public void execute(RepositoryJobExecutionContext context) throws RepositoryException {

        if (!isComponentManagerReady()) {
            return;
        }

        Session session = context.createSystemSession();
        String pollPath = context.getAttribute("pollPath");

        try {
            String storedHash = getStoredHash(session);
            String newhash = getPageHash(pollPath);
            if (!storedHash.equals(newhash)) {
                touchFunnelbackCacheFile(session, newhash);
            }
        } finally {
            session.logout();
        }
    }

    boolean isComponentManagerReady() {
        ComponentManager componentManager = HstServices.getComponentManager();
        if (componentManager == null) {
            return false;
        }

        return componentManager.getContainerConfiguration() != null;
    }

    String getStoredHash(Session session) throws RepositoryException {
        Node cacheHandle = session.getNode(CURATOR_PATH);
        Node published = new HippoUtils().findFirst(cacheHandle.getNodes(), this::isPublished);
        return published.hasProperty(HASH)
                ? published.getProperty(HASH).getString()
                : "";
    }

    boolean isPublished(Node node) throws RepositoryException {
        return "published".equals(node.getProperty("hippostd:state").getString());
    }

    String getPageHash(String pollPath) throws RepositoryException {
        try {
            return doGetPageContentHash(pollPath);
        } catch (IOException | URISyntaxException e) {
            throw new RepositoryException(e);
        }
    }

    String doGetPageContentHash(String pollPath) throws IOException, URISyntaxException {
        URI uri = curatorURI(pollPath);
        String token = HstServices.getComponentManager().getContainerConfiguration().getString("funnelback.token");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(uri);

        try {
            request.addHeader("X-Security-Token", token);
            CloseableHttpResponse response = httpClient.execute(request);
            try {
                InputStream pageInputStream = response.getEntity().getContent();
                return DigestUtils.sha1Hex(pageInputStream);
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }

    URI curatorURI(String pollPath) throws URISyntaxException {
        ContainerConfiguration containerConfiguration = HstServices.getComponentManager().getContainerConfiguration();
        // "/admin-api/curator/v2/collections/govscot~sp-mygov/profiles/_default/curator/"
        String baseUrl = containerConfiguration.getString("funnelback.url");
        return new URIBuilder(baseUrl).setPath(pollPath).build();
    }

    void touchFunnelbackCacheFile(Session session, String hash) throws RepositoryException {
        Node cacheHandle = session.getNode(CURATOR_PATH);
        WorkflowManager wflManager = ((HippoWorkspace) session.getWorkspace()).getWorkflowManager();
        DocumentWorkflow workflow = (DocumentWorkflow) wflManager.getWorkflow("editing", cacheHandle);
        try {
            Document document = workflow.obtainEditableInstance();
            Node node = document.getNode(session);

            recordChange(node, hash);
            session.save();

            workflow.commitEditableInstance();
            session.save();

            workflow.publish();
            session.save();
        }  catch (RemoteException | WorkflowException e) {
            LOG.error("Failed to update funnelback-curator-changes", e);
            throw new RepositoryException(e);
        }
    }

    void recordChange(Node node, String hash) throws RepositoryException {
        node.setProperty(HASH, hash);
        node.setProperty("publishing:lastchangedate", Calendar.getInstance());
    }
}
