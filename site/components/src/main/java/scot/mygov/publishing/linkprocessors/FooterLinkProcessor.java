package scot.mygov.publishing.linkprocessors;

import org.apache.jackrabbit.util.Text;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.linking.HstLinkProcessorTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Link processor for footer items. Conterts urls from /footer/cookies -> /cookes and back again.
 */
public class FooterLinkProcessor extends HstLinkProcessorTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(FooterLinkProcessor.class);

    SessionSource sessionSource = link -> RequestContextProvider.get().getSession();

    /**
     * turn the full inlk into a url e.g. site-furniture/footer/cookies -> cookies
     */
    @Override
    protected HstLink doPostProcess(HstLink link) {
        if ("site-furniture".equals(link.getPathElements()[0])) {
            link.setPath(link.getPathElements()[link.getPathElements().length - 1]);
        }
        return link;
    }

    /**
     * Find the node for this url
     */
    @Override
    protected HstLink doPreProcess(HstLink link) {

        if (link.getPathElements().length != 1) {
            return link;
        }

        try {
            String slug = link.getPathElements()[0];
            Session session = sessionSource.getSession(link);

            String contentPath = String.format("%s/%s/%s", link.getMount().getContentPath(), "site-furniture/footer", Text.escapeIllegalJcr10Chars(slug));
            if (!session.nodeExists(contentPath)) {
                return link;
            }

            Node handle = session.getNode(contentPath);
            link.setPath("footer/" + handle.getName());
            return link;
        } catch (RepositoryException e) {
            LOG.warn("Exception trying to process link: \"{}\"", link.getPath(), e);
            return link;
        }
    }

    // This is just here to support unit testing (to get arround Hippos use of a static method)
    interface SessionSource {
        Session getSession(HstLink link) throws RepositoryException;
    }
}