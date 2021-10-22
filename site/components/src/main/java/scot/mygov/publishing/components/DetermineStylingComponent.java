package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import scot.mygov.publishing.channels.WebsiteInfo;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class DetermineStylingComponent extends CommonComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws RepositoryException {
        super.doBeforeRender(request, response);
        determineStyling(request);
        determineLogo(request);
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

    static void determineLogo (HstRequest request) throws ObjectBeanManagerException, RepositoryException {
        Mount mount = request.getRequestContext().getResolvedMount().getMount();
        WebsiteInfo info = mount.getChannelInfo();

        String logoPath = info.getLogoPath();

        if (!logoPath.isEmpty()) {
            HstRequestContext context = request.getRequestContext();
            Session session = context.getSession();

            Object logo = context.getObjectBeanManager(session).getObject(logoPath);
            request.setAttribute("logo", logo);
        }

    }
}
