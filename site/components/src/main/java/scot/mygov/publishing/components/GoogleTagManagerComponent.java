package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;

public class GoogleTagManagerComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        ResolvedSiteMapItem resolvedSiteMapItem = request.getRequestContext().getResolvedSiteMapItem();
        HstComponentConfiguration hstComponentConfiguration = resolvedSiteMapItem.getHstComponentConfiguration();

        // set gtmName based on the page componenet from the resolved sitemap item
        String gtmName = hstComponentConfiguration.getName();
        request.setAttribute("gtmName", gtmName);

        // set gtmId on the path from the resiolved sitemap item
        String gtmId = resolvedSiteMapItem.getPathInfo();
        request.setAttribute("gtmId", gtmId);

        // set useLiveAnalytics flag depending on if the host group is www
        request.setAttribute("useLiveAnalytics", isLiveHost(request));
    }

    boolean isLiveHost(HstRequest request) {
        String hostGroupName
                = request.getRequestContext().getResolvedMount().getMount().getVirtualHost().getHostGroupName();
        return "live".equals(hostGroupName);
    }



}
