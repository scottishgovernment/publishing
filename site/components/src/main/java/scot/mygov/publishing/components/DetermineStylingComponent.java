package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import scot.mygov.publishing.channels.WebsiteInfo;

public class DetermineStylingComponent extends EssentialsContentComponent {

    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

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
    }
}
