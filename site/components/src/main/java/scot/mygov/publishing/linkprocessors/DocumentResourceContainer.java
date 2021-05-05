package scot.mygov.publishing.linkprocessors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.linking.AbstractResourceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentResourceContainer extends AbstractResourceContainer {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentResourceContainer.class);

    private static final String PUBLISHING_DOCUMENT = "publishing:document";

    private static final String HIPPO_FILENAME = "hippo:filename";

    @Override
    public String getNodeType() {
        return PUBLISHING_DOCUMENT;
    }

    /**
     * Add filenames to document and thumbnail urls
     */
    @Override
    public String resolveToPathInfo(Node resourceContainerNode, Node resourceNode, Mount mount) {
        try {
            String filename = resourceNode.getProperty(HIPPO_FILENAME).getString();
            return new StringBuilder(resourceNode.getPath()).append('/').append(filename).toString();
        } catch (RepositoryException e) {
            LOG.error("Exception processing a container resource link for a publishing:document node type.", e);
            return null;
        }
    }

    @Override
    public Node resolveToResourceNode(Session session, String pathInfo) {
        String path = StringUtils.substringBeforeLast(pathInfo, "/");
        return super.resolveToResourceNode(session, path);
    }
}