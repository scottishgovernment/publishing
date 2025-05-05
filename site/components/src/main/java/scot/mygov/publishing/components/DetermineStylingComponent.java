package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.CommonComponent;
import scot.mygov.publishing.channels.WebsiteInfo;

public class DetermineStylingComponent extends CommonComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        request.setModel(REQUEST_ATTR_DOCUMENT, request.getRequestContext().getContentBean());
        determineStyling(request);
    }

    static void determineStyling(HstRequest request) {
        WebsiteInfo info = MountUtils.websiteInfo(request);

        String style = info.getStyle();
        String css;

        if (style.isEmpty()) {
            // Fallback to compiled "default" CSS
            css = "/assets/themes/sg-brand/css/main.css";
        } else {
            css = "/assets/themes/" + style + "/css/main.css";
        }

        request.setAttribute("css", css);
        request.setAttribute("siteTitle", info.getSiteTitle());
    }
}
