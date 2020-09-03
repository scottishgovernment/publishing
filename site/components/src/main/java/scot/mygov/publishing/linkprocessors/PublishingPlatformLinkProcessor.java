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

        // do not alter binary paths
        if (isBinaryPath(link)) {
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

        // do not alter binary paths
        if (isBinaryPath(link)) {
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
        if (link.getPathElements().length >= 3) {
            return link;
        }

        // if the link has 2 elements then see if it is a guide page
        if (link.getPathElements().length == 2) {
             /// its a guide sub page
            String guideSlug = link.getPathElements()[0];
            String guidePath = lookupPath(link, guideSlug);
            if (guidePath != null) {
                guidePath = StringUtils.substringBefore(guidePath, "/index");
                String guidPage = link.getPathElements()[1];
                link.setPath(guidePath + "/" + guidPage);
            }
            return link;
        }

        String path = lookupPath(link, link.getPath());
        if (path != null) {
            link.setPath(path);
        }
        return link;
    }

    String lookupPath(HstLink link, String path) throws RepositoryException {
        Session session = sessionSource.getSession();
        String lp = slugLookupPath(link, path);
        if (!session.nodeExists(lp)) {
            return null;
        }
        Node lookupNode = session.getNode(lp);
        if (!lookupNode.hasProperty("publishing:path")) {
            return null;
        }

        return lookupNode.getProperty("publishing:path").getString();
    }

    String slugLookupPath(HstLink link) {
        return slugLookupPath(siteName(link), link.getMount().getType(), link.getPath());
    }

    String slugLookupPath(HstLink link, String path) {
        return slugLookupPath(siteName(link), link.getMount().getType(), path);
    }

    String slugLookupPath(String siteName, String mountType, String path) {
        StringBuilder b = new StringBuilder()
                .append("/content/urls/")
                .append(siteName)
                .append('/')
                .append(mountType)
                .append('/');
        appendSlugAsLetterslugLetterPath(b, path);
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

    boolean isBinaryPath(HstLink link) {
        return link.getPath().startsWith("binaries/");
    }
}
