package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

public class FeedbackComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        request.setAttribute("layoutName", getLayoutName(request));
    }

    String getLayoutName(HstRequest request) {
        return request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getHstComponentConfiguration()
                .getName();
    }
}
