package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;

import scot.mygov.publishing.beans.PhaseBanner;
import scot.mygov.publishing.channels.WebsiteInfo;

import java.util.List;

/**
 * Set fields required to suppress the header search on some formats.
 *
 * We set the following attributes:
 * - hideSearch - whether to hide the search form
 */
public class SiteHeaderComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        setWebsiteInfo(request);
        setPhaseBanner(request);
        setCanonical(request);
    }

    static void setWebsiteInfo(HstRequest request) {
        Mount mount = request.getRequestContext().getResolvedMount().getMount();
        WebsiteInfo info = mount.getChannelInfo();
        request.setAttribute("siteTitle", info.getSiteTitle());
        request.setAttribute("displaySiteTitleInHeader", info.isDisplaySiteTitleInHeader());
        request.setAttribute("logo", info.getLogo());
        request.setAttribute("logoAltText", info.getLogoAltText());
    }

    static void setPhaseBanner(HstRequest request) {
        HippoFolder bannersFolder = folder(request, "site-furniture/banners");
        List<PhaseBanner> phaseBanners = bannersFolder.getDocuments(PhaseBanner.class);
        request.setAttribute("phasebanner", phaseBanners.isEmpty() ? null : phaseBanners.get(0));
    }

    static HippoFolder folder(HstRequest request, String path) {
        HstRequestContext c = request.getRequestContext();
        HippoBean root = c.getSiteContentBaseBean();
        return root.getBean(path, HippoFolder.class);
    }

    static void setCanonical(HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        HstLinkCreator linkCreator = context.getHstLinkCreator();
        HippoBean contentBean = context.getContentBean();

        if (contentBean == null) {
            contentBean = context.getSiteContentBaseBean();
        }
        List<HstLink>  links = linkCreator.createAllAvailableCanonicals(
                contentBean.getNode(), context, "live", "production");
        if (!links.isEmpty()) {
            HstLink link = links.get(0);
            String canonical = link.toUrlForm(context, true);
            request.setAttribute("canonical", canonical);
        }

    }

}
