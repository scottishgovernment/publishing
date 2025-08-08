package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import scot.mygov.publishing.channels.WebsiteInfo;

public class FeedbackComponent extends EssentialsContentComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        request.setAttribute("layoutName", getLayoutName(request));
        request.setAttribute("isFeedbackEnabled", isFeedbackEnabled(request));
        request.setAttribute("feedbackDocument", feedbackDocument(request));
    }

    String getLayoutName(HstRequest request) {
        return request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getHstComponentConfiguration()
                .getName();
    }

    boolean isFeedbackEnabled(HstRequest request) {

        if (SiteSettings.feedbackEnabled(request)) {
            return false;
        }

        // now check if this page has feeback enabled
        HippoBean bean = request.getRequestContext().getContentBean();
        return bean != null && bean.getSingleProperty("publishing:showFeedback", false);
    }

    HippoBean feedbackDocument(HstRequest request) {
        HippoBean site = request.getRequestContext().getSiteContentBaseBean();
        return site.getBean("site-furniture/feedback");
    }
}
