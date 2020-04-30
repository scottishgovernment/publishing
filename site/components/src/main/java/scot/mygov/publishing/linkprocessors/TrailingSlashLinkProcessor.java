package scot.mygov.publishing.linkprocessors;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.linking.HstLinkProcessorTemplate;

public class TrailingSlashLinkProcessor extends HstLinkProcessorTemplate {

    protected HstLink doPostProcess(final HstLink link) {
        // if the path has an extension then leave it alone
        String extension = StringUtils.substringAfterLast(link.getPath(), ".");
        if (StringUtils.isNotEmpty(extension)) {
            return link;
        }

        // if the path ends in a slash then leave it alone
        if (!link.getPath().endsWith("/")) {
            return link;
        }

        // wrap the link in order to add a trailing slash
        return new PublishingLink(link);
    }

    @Override
    protected HstLink doPreProcess(HstLink link) {
        return link;
    }

}
