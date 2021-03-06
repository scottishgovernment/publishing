package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

import static org.apache.commons.lang3.StringUtils.equalsAny;

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
        HstComponentConfiguration componentConfig = request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getHstComponentConfiguration();
        String formatName = componentConfig.getName();
        // hide search for home or search pages
        request.setAttribute("hideSearch", equalsAny(formatName, "home", "search"));
    }

}
