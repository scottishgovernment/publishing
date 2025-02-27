package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.CommonComponent;

@ParametersInfo(type = HomeCategoriesComponentInfo.class)
public class HomeCategoriesComponent extends CommonComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
        HippoFolderBean folder = baseBean.getBean("browse");
        request.setAttribute("children", CategoryComponent.getChildren(folder));
    }

}