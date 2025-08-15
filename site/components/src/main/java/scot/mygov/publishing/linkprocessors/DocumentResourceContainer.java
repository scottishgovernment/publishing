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
     * Clean up urls for document cover page documents and thumbnails.
     */
    @Override
    public String resolveToPathInfo(Node resourceContainerNode, Node resourceNode, Mount mount) {
        try {
            if ("publishing:thumbnails".equals(resourceNode.getName())) {
                return resourceNode.getPath();
            }

            String pathInfo = resourceNode.getPath();
            String filename = resourceNode.getProperty(HIPPO_FILENAME).getString();
            return  StringUtils.substringAfter(pathInfo, "/content/documents") + "/" + filename;
        } catch (RepositoryException e) {
            LOG.error("Exception processing a container resource link for a publishing:document node type.", e);
            return null;
        }
    }

    @Override
    public Node resolveToResourceNode(Session session, String pathInfo) {

        try {
            if (StringUtils.endsWith(pathInfo, "/publishing:thumbnails")) {
                return session.getNode(pathInfo);
            }

            if (session.nodeExists(pathInfo)) {
                return super.resolveToResourceNode(session, pathInfo);
            }

            String path = stripFilename(pathInfo);
            path = "/content/documents" + path;
            if (session.nodeExists(path)) {
                return session.getNode(path);
            }
            return super.resolveToResourceNode(session, pathInfo);
        } catch (RepositoryException e) {
            LOG.error("Exception processing a container resource link for a publishing:document node type.", e);
        }
        return super.resolveToResourceNode(session, pathInfo);
    }

    public static String stripFilename(String path) {
        if (StringUtils.isBlank(path)) {
            return path;
        }

        path = StringUtils.replace(path, "\\", "/");
        path = StringUtils.removeEnd(path, "/");

        int lastSlash = StringUtils.lastIndexOf(path, '/');
        if (lastSlash == -1) {
            return path;
        }

        String lastPart = StringUtils.substring(path, lastSlash + 1);
        if (StringUtils.contains(lastPart, ".")) {
            return StringUtils.substring(path, 0, lastSlash);
        }
        return path;
    }
}