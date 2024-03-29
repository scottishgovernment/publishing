package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

import java.util.ArrayList;
import java.util.List;

import org.hippoecm.hst.core.request.HstRequestContext;
import scot.mygov.publishing.beans.MourningBanner;
import scot.mygov.publishing.beans.NotificationBanner;

import static java.util.stream.Collectors.toList;

public class NotificationBannersComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response){
        HippoFolder bannersFolder = folder(request, "site-furniture/banners");
        HippoBean contentBean = request.getRequestContext().getContentBean();

        request.setAttribute("cookiespage", cookiepage(request));

        if (bannersFolder == null) {
            request.setAttribute("notificationbanners", new ArrayList<>());
            request.setAttribute("mourningbanner", null);
            return;
        }

        List<NotificationBanner> banners = getIncludedBanners(bannersFolder, contentBean);
        MourningBanner mourningBanner = getMourningBanner(bannersFolder);
        request.setAttribute("mourningbanner", mourningBanner);
        request.setAttribute("notificationbanners", banners);
    }

    static MourningBanner getMourningBanner(HippoFolder folder) {
        List<MourningBanner> mourningBanners = folder.getDocuments(MourningBanner.class);
        return mourningBanners.isEmpty() ? null : mourningBanners.get(0);
    }

    static HippoBean cookiepage(HstRequest request) {
        return request.getRequestContext().getSiteContentBaseBean().getBean("site-furniture/footer/cookies");
    }

    static HippoFolder folder(HstRequest request, String path) {
        HstRequestContext c = request.getRequestContext();
        HippoBean root = c.getSiteContentBaseBean();
        return root.getBean(path, HippoFolder.class);
    }

    static List<NotificationBanner> getIncludedBanners(HippoFolder folder, HippoBean contentBean) {
        return folder.getDocuments(NotificationBanner.class)
                .stream()
                .filter(banner -> showBanner(banner, contentBean))
                .collect(toList());
    }

    static boolean showBanner(NotificationBanner banner, HippoBean contentBean)  {
        if (contentBean == null) {
            return true;
        }
        return banner.getExcluded()
                .stream()
                .noneMatch(b -> b.isSelf(contentBean));
    }

}
