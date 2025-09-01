package scot.mygov.publishing.components;

import org.apache.commons.lang3.StringUtils;
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

        HomeCategoriesComponentInfo paramInfo = getComponentParametersInfo(request);
        if (StringUtils.isNotBlank(paramInfo.getCategory())) {
            HippoBean category = baseBean.getBean(paramInfo.getCategory());
            request.setAttribute("document", category);
            folder = (HippoFolderBean) category.getParentBean();
        }
        request.setAttribute("children", CategoryComponent.getChildren(folder));
        request.setAttribute("navigationType", paramInfo.getNavigationType());
        request.setAttribute("backgroundcolor", paramInfo.getBackgroundColor());
        request.setAttribute("removebottompadding", paramInfo.getRemoveBottomPadding());
    }

}