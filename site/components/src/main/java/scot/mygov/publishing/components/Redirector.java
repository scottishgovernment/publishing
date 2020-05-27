package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.util.HstResponseUtils;

/**
 * Extract the job of sending a redirect to aid unit testing (avoind sstaic method call.
 */
class Redirector {

    void sendPermanentRedirect(HstRequest request, HstResponse response, String url) {
        HstResponseUtils.sendPermanentRedirect(request, response, url);
    }

}
