package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.EssentialsDocumentComponent;

@ParametersInfo(type = ImageAndTextComponentInfo.class)
public class ImageAndTextComponent extends EssentialsDocumentComponent {

    // nothing required - the document will be available in the request,
    // and the other options from the params info as 'cparam', for example 'cparam.theme'.
    @Override
    public void doBeforeRender(HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);
        ImageAndTextComponentInfo paramInfo = getComponentParametersInfo(request);
        request.setAttribute("backgroundcolor", paramInfo.getBackgroundColor());
        request.setAttribute("verticalcenter", paramInfo.getVerticalCenter());
        request.setAttribute("removebottompadding", paramInfo.getRemoveBottomPadding());
    }
}
