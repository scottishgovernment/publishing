package scot.mygov.publishing.linkprocessors;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.linking.AbstractResourceContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scot.mygov.publishing.HippoUtils;

public class DocumentResourceContainer extends AbstractResourceContainer {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentResourceContainer.class);

    private static final String PUBLISHING_DOCUMENT = "publishing:document";

    private static final String HIPPO_FILENAME = "hippo:filename";

    private HippoUtils hippoUtils = new HippoUtils();

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
            String pathInfo = resourceContainerNode.getPath();

            if (pathInfo == null) {
                return pathInfo;
            }

            String filename = resourceNode.getProperty(HIPPO_FILENAME).getString();
            String path = resourceContainerNode.getParent().getParent().getPath();
            return path + "/" + filename;
        } catch (RepositoryException e) {
            LOG.error("Exception processing a container resource link for a publishing:document node type.", e);
            return null;
        }
    }

    @Override
    public Node resolveToResourceNode(Session session, String pathInfo) {
        int lastSlash = pathInfo.lastIndexOf('/');
        String name = pathInfo.substring(lastSlash + 1);
        String path = pathInfo.substring(0, lastSlash);

        try {
            Node handle = session.getNode(path);
            Node publishedVariant = getVariant(handle);
            Node documentWithFilename = findNodeWithFilename(publishedVariant, name);
            return documentWithFilename;
        } catch (RepositoryException e) {
            LOG.error("Exception processing a container resource link for a publishing:document node type.", e);
        }

        return super.resolveToResourceNode(session, pathInfo);
    }

    Node getVariant(Node handle) throws RepositoryException {
        Node publishedVariant = hippoUtils.getPublishedVariant(handle);
        return publishedVariant != null ? publishedVariant : handle.getNodes().nextNode();
    }

    Node findNodeWithFilename(Node coverPage, String name) throws RepositoryException {

        // find either a document or a thumbnail
        NodeIterator nodeIterator = coverPage.getNodes("publishing:documents*");
        while (nodeIterator.hasNext()) {
            Node documentNode = nodeIterator.nextNode();
            Node resourceWithfilename = hippoUtils.find(documentNode.getNodes(), child -> hasFilename(child, name));
            if (resourceWithfilename != null) {
                return resourceWithfilename;
            }
        }
        return null;
    }

    boolean hasFilename(Node node, String filename) throws RepositoryException {
        String nodeFileName = node.getProperty(HIPPO_FILENAME).getString();
        return filename.equals(nodeFileName);
    }
}