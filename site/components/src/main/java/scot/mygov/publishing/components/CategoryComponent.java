package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;

import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * Component used to back category pages (including the home page).
 *
 * This component makes a list of its navigable children available as the "children" attribute. This list contains the
 * bean for any children, with the index bean being added in the place of any sub-categories.  This includes subcategories
 * and any articles etc.
 */
public class CategoryComponent extends EssentialsContentComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        HippoBean bean = request.getRequestContext().getContentBean();
        HippoFolderBean folder = (HippoFolderBean) bean.getParentBean();
        List<HippoBean> children = getChildren(folder);
        request.setAttribute("children", children);
    }

    static List<HippoBean> getChildren(HippoFolderBean folder) {
        return folder
                .getChildBeans(HippoBean.class)
                .stream()
                .filter(CategoryComponent::notIndexFile)
                .filter(CategoryComponent::notFooter)
                .map(CategoryComponent::mapBean)
                .collect(toList());
    }

    static boolean notIndexFile(HippoBean bean) {
        return !"index".equals(bean.getName());
    }

    static boolean notFooter(HippoBean bean) {
        return !"footer".equals(bean.getName());
    }

    /**
     * Map the bean to use for this lint.  If it is a fodler then return the index file, otherwise just use this bean.
     */
    static HippoBean mapBean(HippoBean bean) {
        return bean.isHippoFolderBean()
                ? indexBean(bean)
                : bean;
    }

    static HippoBean indexBean(HippoBean bean) {
        return bean.getBean("index");
    }
}