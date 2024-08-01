package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import scot.mygov.publishing.channels.WebsiteInfo;

public class FeedbackComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        request.setAttribute("layoutName", getLayoutName(request));

        WebsiteInfo info = MountUtils.websiteInfo(request);
        request.setAttribute("isFeedbackEnabled", info.isFeedbackEnabled());
    }

    String getLayoutName(HstRequest request) {
        return request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getHstComponentConfiguration()
                .getName();
    }
}
