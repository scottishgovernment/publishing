package scot.mygov.publishing.linkprocessors;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Link processor used to implement our URL guidelines. Aticles are served at their unique slug, categoeries at their
 * natural url.
 */
public class PublishingPlatformLinkProcessor implements HstLinkProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PublishingPlatformLinkProcessor.class);

    private static final String SLUG = "publishing:slug";

    private static final String INDEX = "index";

    SessionSource sessionSource = () -> RequestContextProvider.get().getSession();

    @Override
    public HstLink postProcess(final HstLink link) {

        // do not alter paths with extensions
        if (hasExtension(link)) {
            return link;
        }

        try {
            return doPostProcess(link);
        } catch (RepositoryException e) {
            LOG.error("Failed to post process link {}", link.getPath(), e);
            return link;
        }
    }

    HstLink doPostProcess(HstLink link) throws RepositoryException {

        // we need to consider guides. Do we need special case for guide folder?
        // in the case where is uses the slug, for guide pages we need to use guideslug/slug

        Session session = sessionSource.getSession();
        String contentPath = pathForNode(link);

        if (!session.nodeExists(contentPath)) {
            return link;
        }

        Node node = session.getNode(contentPath);
        if (isGuideFolder(node)) {
            Node guide = node.getNode(INDEX).getNode(INDEX);
            setPath(link, guide);
            return link;
        }

        if (node.isNodeType("hippostd:folder")) {
            return link;
        }

        Node variant = node.getNode(node.getName());
        if (variant.isNodeType("publishing:guidepage")) {
            setPathForGuidePage(link, variant);
            return link;
        }

        if (variant.hasProperty(SLUG)) {
            setPath(link, variant);
            return link;
        }

        return link;
    }

    boolean isGuideFolder(Node node) throws RepositoryException {
        if (!node.isNodeType("hippostd:folder")) {
            return false;
        }

        if (!node.hasNode(INDEX)) {
            return false;
        }
        Node index = node.getNode(INDEX).getNode(INDEX);
        return index.isNodeType("publishing:guide");
    }

    void setPathForGuidePage(HstLink link, Node guidepage) throws RepositoryException {
        Node handle = guidepage.getParent();
        Node guideFolder = handle.getParent();
        Node guide = guideFolder.getNode(INDEX).getNode(INDEX);

        String guideSlug = guide.getProperty(SLUG).getString();
        if (isFirstGuidePage(guideFolder, handle)) {
            link.setPath(new StringBuffer("/").append(guideSlug).toString());
        } else {
            link.setPath(new StringBuffer("/").append(guideSlug).append("/").append(guidepage.getName()).toString());
        }
    }

    boolean isFirstGuidePage(Node folder, Node handle) throws RepositoryException {
        Node firstGuidePageHandle = firstGuidePageHandle(folder);
        return handle.isSame(firstGuidePageHandle);
    }

    Node firstGuidePageHandle(Node folder) throws RepositoryException {
        NodeIterator it = folder.getNodes();
        while (it.hasNext()) {
            Node child = it.nextNode();
            Node variant = child.getNodes().nextNode();
            if (variant.isNodeType("publishing:guidepage")) {
                return child;
            }
        }
        return null;
    }

    void setPath(HstLink link, Node page) throws RepositoryException {
        String slug = page.getProperty(SLUG).getString();
        link.setPath(new StringBuffer("/").append(slug).toString());
    }

    String pathForNode(HstLink link) {
        String contentPath = link.getMount().getContentPath();
        return new StringBuffer()
                .append(contentPath)
                .append('/')
                .append(link.getPath())
                .toString();
    }

    @Override
    public HstLink preProcess(HstLink link) {

        // do not alter links with extensions
        if (hasExtension(link)) {
            return link;
        }

        try {
            return doPreProcess(link);
        } catch (RepositoryException e) {
            LOG.error("Failed to pre process link {}", link.getPath(), e);
            return link;
        }
    }

    HstLink doPreProcess(HstLink link) throws RepositoryException {
        Session session = sessionSource.getSession();
        String lookupPath = slugLookupPath(link);

        if (!session.nodeExists(lookupPath)) {
            return link;
        }

        Node lookupNode = session.getNode(lookupPath);
        if (!lookupNode.hasProperty("publishing:path")) {
            return link;
        }

        String path = lookupNode.getProperty("publishing:path").getString();
        link.setPath(path);
        return link;
    }

    String slugLookupPath(HstLink link) {
        StringBuilder b = new StringBuilder()
                .append("/content/urls/")
                .append(siteName(link))
                .append('/')
                .append(link.getMount().getType())
                .append('/');
        appendSlugAsLetterslugLetterPath(b, link.getPath());
        return b.toString();
    }

    StringBuilder appendSlugAsLetterslugLetterPath(StringBuilder b, String slug) {
        for(char c : slug.toCharArray()) {
            b.append(escapeSlash(c));
            b.append('/');
        }
        return b;
    }

    private Character escapeSlash(char c) {
        return c == '/' ? '\\' : c;
    }

    String siteName(HstLink link) {
        Mount mount = link.getMount();
        String contentPath = mount.getContentPath();
        return StringUtils.substringAfterLast(contentPath, "/");
    }

    boolean hasExtension(HstLink link) {
        String extension = StringUtils.substringAfterLast(link.getPath(), ".");
        return StringUtils.isNotEmpty(extension);
    }
}
