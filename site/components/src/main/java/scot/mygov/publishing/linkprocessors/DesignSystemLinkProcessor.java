package scot.mygov.publishing.linkprocessors;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.linking.HstLinkProcessorTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.apache.commons.lang3.StringUtils.substringAfter;

public class DesignSystemLinkProcessor extends HstLinkProcessorTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(DesignSystemLinkProcessor.class);

    @Override
    protected HstLink doPostProcess(HstLink link) {

        if (!isDesignSystemLink(link)) {
            return link;
        }

        if (!isCategoryLink(link)) {
            return link;
        }

        // remove category
        link.setPath(String.format("%s", link.getPathElements()[1]));
        return link;
    }

    private boolean isDesignSystemLink(HstLink link) {
        // best way to identify?  Name of the mount?
        return true;
    }

    private boolean isCategoryLink(HstLink link) {
        //does it start with
        return StringUtils.startsWith(link.getPath(), "/categories");
    }

    @Override
    protected HstLink doPreProcess(HstLink link) {

        if (!isDesignSystemLink(link)) {
            return link;
        }

        try {
            // add the category to the path
            String testpath = "/content/documents/designsystem/categories/" + link.getPath();
            HstRequestContext req = RequestContextProvider.get();
            Session session = req.getSession();
            if (!session.nodeExists(testpath)) {
                return link;
            }

            link.setPath(substringAfter(testpath, "/content/documents/designsystem"));
            return link;
        } catch (RepositoryException e) {
            LOG.warn("Exception trying to process link: \"{}\"", link.getPath(), e);
            return link;
        }
    }

}