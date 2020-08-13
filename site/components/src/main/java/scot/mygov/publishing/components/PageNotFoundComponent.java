package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

/*
    This component is modelled after the EssentialsPageNotFoundComponent.
    It extends the DetermineStylingComponent to ensure that the channel
    info for styling can still be used on 404 pages.
 */

public class PageNotFoundComponent extends DetermineStylingComponent {

    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        response.setStatus(404);
    }
}
