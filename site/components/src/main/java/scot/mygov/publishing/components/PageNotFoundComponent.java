package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
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
        DetermineStylingComponent.determineStyling(request);
        SiteHeaderComponent.determineLogo(request);
    }
}
