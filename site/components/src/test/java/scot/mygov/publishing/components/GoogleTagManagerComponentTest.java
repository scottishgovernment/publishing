package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.configuration.hosting.VirtualHost;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedMount;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created by z418868 on 25/10/2019.
 */
public class GoogleTagManagerComponentTest {

    @Test
    public void setsExpectedValuesForLiveHost() {

        // ARRANGE
        GoogleTagManagerComponent sut = new GoogleTagManagerComponent();
        HstRequest request = request("expectedGtmName", "expectedGtmId", "live");
        HstResponse response = mock(HstResponse.class);

        // ACT
        sut.doBeforeRender(request, response);

        // ASSERT
        verify(request).setAttribute("gtmName", "expectedGtmName");
        verify(request).setAttribute("gtmId", "expectedGtmId");
        verify(request).setAttribute("useLiveAnalytics", true);
    }

    @Test
    public void setsExpectedValuesForNonLiveHost() {

        // ARRANGE
        GoogleTagManagerComponent sut = new GoogleTagManagerComponent();
        HstRequest request = request("expectedGtmName", "expectedGtmId", "intwww");
        HstResponse response = mock(HstResponse.class);

        // ACT
        sut.doBeforeRender(request, response);

        // ASSERT
        verify(request).setAttribute("gtmName", "expectedGtmName");
        verify(request).setAttribute("gtmId", "expectedGtmId");
        verify(request).setAttribute("useLiveAnalytics", false);
    }

    HstRequest request(String configName, String pathInfo, String hostGroupName) {
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        ResolvedSiteMapItem resolvedSiteMapItem = mock(ResolvedSiteMapItem.class);
        HstComponentConfiguration hstComponentConfiguration = mock(HstComponentConfiguration.class);
        ResolvedMount resolvedMount = mock(ResolvedMount.class);
        Mount mount = mock(Mount.class);
        VirtualHost virtualHost = mock(VirtualHost.class);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getResolvedSiteMapItem()).thenReturn(resolvedSiteMapItem);
        when(resolvedSiteMapItem.getHstComponentConfiguration()).thenReturn(hstComponentConfiguration);
        when(resolvedSiteMapItem.getPathInfo()).thenReturn(pathInfo);
        when(hstComponentConfiguration.getName()).thenReturn(configName);
        when(context.getResolvedMount()).thenReturn(resolvedMount);
        when(resolvedMount.getMount()).thenReturn(mount);
        when(mount.getVirtualHost()).thenReturn(virtualHost);
        when(virtualHost.getHostGroupName()).thenReturn(hostGroupName);
        request.getRequestContext().getResolvedMount().getMount().getVirtualHost().getHostGroupName();
        return request;
    }

}
