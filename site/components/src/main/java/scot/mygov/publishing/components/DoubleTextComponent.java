package scot.mygov.publishing.components;

import com.google.common.base.Strings;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;

@ParametersInfo(type = DoubleTextComponentInfo.class)
public class DoubleTextComponent extends CommonComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        DoubleTextComponentInfo paramInfo = getComponentParametersInfo(request);
        setDocument("document1", paramInfo.getDocument1(), request);
        setDocument("document2", paramInfo.getDocument2(), request);
        request.setAttribute("alignment1", paramInfo.getAlignment1());
        request.setAttribute("alignment2", paramInfo.getAlignment2());
    }

    void setDocument(String attr, String documentPath, HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        if (!Strings.isNullOrEmpty(documentPath)) {
            HippoBean root = context.getSiteContentBaseBean();
            request.setModel(attr, root.getBean(documentPath));
        }
    }

}
