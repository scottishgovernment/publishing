package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.channels.WebsiteInfo;

public class DetermineStylingComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(DetermineStylingComponent.class);

    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        try {
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
        } catch (HstComponentException e) {
            LOG.error("Failed to set CSS path request attribute: {}", e);
        }
    }
}
