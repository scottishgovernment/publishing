package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

public class PreviewIndicatorComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        Mount resolvedMount = request.getRequestContext().getResolvedMount().getMount();
        if("preview".equals(resolvedMount.getType()) && resolvedMount.getAlias().endsWith("-staging")){
            request.setAttribute("isStaging", true);
        }
    }
}
