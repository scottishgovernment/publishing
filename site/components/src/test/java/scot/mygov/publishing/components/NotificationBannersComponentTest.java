package scot.mygov.publishing.components;


import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.junit.Test;
import org.mockito.Mockito;
import scot.mygov.publishing.beans.MourningBanner;
import scot.mygov.publishing.beans.NotificationBanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 08/07/2020.
 */
public class NotificationBannersComponentTest {

    @Test
    public void correctBannersAreSet() {

        // ARRANGE
        NotificationBannersComponent sut = new NotificationBannersComponent();
        List<NotificationBanner> banners = new ArrayList<>();
        HippoBean contentBean = mock(HippoBean.class);
        HippoBean anotherBean = mock(HippoBean.class);
        NotificationBanner excludedBanner = bannerWithExclusion(contentBean);
        NotificationBanner includedBanner = bannerWithExclusion(anotherBean);
        Collections.addAll(banners, excludedBanner, includedBanner);
        HstRequest request = requestWithBanners(banners,  contentBean);
        HstResponse response = anyResponse();

        List<NotificationBanner> expectedBanners = new ArrayList<>();
        expectedBanners.add(includedBanner);

        // ACT
        sut.doBeforeRender(request, response);

        // ASSERT
        Mockito.verify(request).setAttribute("notificationbanners", expectedBanners);
    }

    @Test
    public void addsMourningBannerIfPresent() {

        // ARRANGE
        NotificationBannersComponent sut = new NotificationBannersComponent();
        HippoBean contentBean = mock(HippoBean.class);
        MourningBanner mourningBanner = mock(MourningBanner.class);
        HstRequest request = requestWithMourningBanner(mourningBanner,  contentBean);
        HstResponse response = anyResponse();

        // ACT
        sut.doBeforeRender(request, response);

        // ASSERT
        Mockito.verify(request).setAttribute("mourningbanner", mourningBanner);
    }

    @Test
    public void showBannerDefaultsToTrueForNoContentBean() {
        assertTrue(NotificationBannersComponent.showBanner(anyBanner(), null));
    }

    @Test
    public void showBannerReturnsFalseIfContentBeanIsExcluded() {
        HippoBean contentBean = mock(HippoBean.class);
        NotificationBanner banner = bannerWithExclusion(contentBean);
        assertFalse(NotificationBannersComponent.showBanner(banner, contentBean));
    }

    HstRequest requestWithBanners(List<NotificationBanner> banners, HippoBean contentBean) {
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        HippoBean contentBaseBean = mock(HippoBean.class);
        HippoFolder folder = mock(HippoFolder.class);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(contentBean);
        when(context.getSiteContentBaseBean()).thenReturn(contentBaseBean);
        when(contentBaseBean.getBean("site-furniture/banners", HippoFolder.class)).thenReturn(folder);
        when(folder.getDocuments((NotificationBanner.class))).thenReturn(banners);
        return request;
    }

    HstRequest requestWithMourningBanner(MourningBanner banner, HippoBean contentBean) {
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        HippoBean contentBaseBean = mock(HippoBean.class);
        HippoFolder folder = mock(HippoFolder.class);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(contentBean);
        when(context.getSiteContentBaseBean()).thenReturn(contentBaseBean);
        when(contentBaseBean.getBean("site-furniture/banners", HippoFolder.class)).thenReturn(folder);
        when(folder.getDocuments((MourningBanner.class))).thenReturn(Collections.singletonList(banner));
        return request;
    }

    NotificationBanner bannerWithExclusion(HippoBean excluded) {
        NotificationBanner banner = mock(NotificationBanner.class);
        when(excluded.isSelf(excluded)).thenReturn(true);
        when(banner.getExcluded()).thenReturn(singletonList(excluded));
        return banner;
    }

    HstResponse anyResponse() {
        return mock(HstResponse.class);
    }

    NotificationBanner anyBanner() {
        return Mockito.mock(NotificationBanner.class);
    }

}
