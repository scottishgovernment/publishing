package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.EssentialsPageNotFoundComponent;

/*
    This component is modelled after the EssentialsPageNotFoundComponent.
    It uses the DetermineStylingComponent to ensure that the channel
    info for styling can still be used on 404 pages.
    It uses the SiteHeaderComponent to ensure the logo is used from the
    channel info on 404s where appropriate
 */

public class PageNotFoundComponent extends EssentialsPageNotFoundComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        if (request.getRequestContext().getContentBean() == null) {
            set404Document(request);
        }
        DetermineStylingComponent.determineStyling(request);
        SiteHeaderComponent.setWebsiteInfo(request);
    }

    static void set404Document(HstRequest request) {
        request.setAttribute(REQUEST_ATTR_DOCUMENT, notFoundBean(request));
    }

    static HippoBean notFoundBean(HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        HippoBean siteBean = context.getSiteContentBaseBean();
        return siteBean.getBean("site-furniture/status/404");
    }
}
