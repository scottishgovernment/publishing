package scot.mygov.publishing.components;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;

public class SiteSettings {

    static boolean feedbackEnabled(HstRequest request) {
        return siteSettings(request).getSingleProperty("publishing:feedbackEnabled");
    }

    static String searchType(HstRequest request) {
        HippoBean siteSettings = siteSettings(request);
        return siteSettings.getSingleProperty("publishing:searchType");
    }

    static boolean hasSearch(HstRequest request) {
        return !StringUtils.equals(searchType(request), "none");
    }

    static HippoBean siteSettings(HstRequest request) {
        HippoBean site = request.getRequestContext().getSiteContentBaseBean();
        HippoBean administration = site.getBean("administration");
        return administration.getBean("site-settings");
    }
}
