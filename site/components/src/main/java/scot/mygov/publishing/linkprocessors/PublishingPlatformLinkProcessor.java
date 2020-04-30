package scot.mygov.publishing.linkprocessors;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Link processor used to implement our URL guidelines. Aticles are served at their unique slug, categoeries at their
 * natural url.
 */
public class PublishingPlatformLinkProcessor implements HstLinkProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PublishingPlatformLinkProcessor.class);

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

        Session session = sessionSource.getSession();
        String contentPath = pathForNode(link);
        Node node = session.getNode(contentPath);
        if (node.isNodeType("hippostd:folder")) {
            return link;
        }

        Node variant = node.getNode(node.getName());
        link.setPath(variant.getProperty("publishing:slug").getString());
        return link;
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

        if (link.getPathElements().length > 1) {
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
        String slug = link.getPathElements()[0];
        appendSlugAsLetterslugLetterPath(b, slug);
        return b.toString();
    }

    StringBuilder appendSlugAsLetterslugLetterPath(StringBuilder b, String slug) {
        for(char c : slug.toCharArray()) {
            b.append(c);
            b.append('/');
        }
        return b;
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
