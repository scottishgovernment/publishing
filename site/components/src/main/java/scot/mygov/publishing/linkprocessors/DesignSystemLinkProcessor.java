package scot.mygov.publishing.linkprocessors;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.linking.HstLinkProcessorTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.apache.commons.lang3.StringUtils.substringAfter;

public class DesignSystemLinkProcessor extends HstLinkProcessorTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(DesignSystemLinkProcessor.class);

    SessionSource sessionSource = () -> RequestContextProvider.get().getSession();

    private boolean isDesignSystemLink(HstLink link) {
        return "designsystem".equals(link.getMount().getHstSite().getName());
    }

    private boolean isBrowseLink(HstLink link) {
        return link.getPath().startsWith("browse/");
    }

    @Override
    protected HstLink doPostProcess(HstLink link) {

        if (!isDesignSystemLink(link)) {
            return link;
        }

        if (!isBrowseLink(link)) {
            return link;
        }

        link.setPath(link.getPath().replace("browse/", ""));
        return link;
    }

    @Override
    protected HstLink doPreProcess(HstLink link) {

        if (!isDesignSystemLink(link)) {
            return link;
        }

        try {
            // add 'browse' to the path
            Session session = sessionSource.getSession();
            String contentPath = "/content/documents/designsystem/browse/" + link.getPath();
            if (!session.nodeExists(contentPath)) {
                return link;
            }

            link.setPath(substringAfter(contentPath, "/content/documents/designsystem"));
            return link;

        } catch (RepositoryException e) {
            LOG.warn("Exception trying to process link: \"{}\"", link.getPath(), e);
            return link;
        }
    }

}