package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.hosting.Mount;
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
        Mount mount = request.getRequestContext().getResolvedMount().getMount();
        WebsiteInfo info = mount.getChannelInfo();

        String style = info.getStyle();
        String css;

        if (style.isEmpty()) {
            // Fallback to compiled "default" CSS
            css = "/assets/css/main.css";
        } else {
            css = "/assets/" + style + "/css/main.css";
        }

        request.setAttribute("css", css);
        request.setAttribute("siteTitle", info.getSiteTitle());
    }
}
