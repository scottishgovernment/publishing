package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.*;

/**
 * Component used to back category pages (including the home page).
 *
 * This component makes a list of its navigable children available as the "children" attribute. This list contains the
 * bean for any children, with the index bean being added in the place of any sub-categories.  This includes subcategories
 * and any articles etc.
 *
 * The footer and administration folders are excluded.
 */
public class CategoryComponent extends EssentialsContentComponent {

    static final String INDEX = "index";

    static final Set<String> EXCLUDED = new HashSet<>();

    static {
        // node names that should not be included on the home page
        Collections.addAll(EXCLUDED, "administration", "site-furniture");
    }

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        setCategoryAttributes(request);
    }

    static void setCategoryAttributes(HstRequest request) {

        if (!hasContentBean(request)) {
            return;
        }

        HippoBean bean = request.getRequestContext().getContentBean();
        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
        HippoFolderBean folder = (HippoFolderBean) bean.getParentBean();
        List<HippoBean> children = getChildren(folder, baseBean);
        request.setAttribute("children", children);
    }

    static boolean hasContentBean(HstRequest request) {
        return request.getRequestContext().getContentBean() != null;
    }

    static List<HippoBean> getChildren(HippoFolderBean folder, HippoBean baseBean) {

        return folder
                .getChildBeans(HippoBean.class)
                .stream()
                .filter(CategoryComponent::notIndexFile)
                .filter(node -> !excluded(node, folder, baseBean))
                .map(CategoryComponent::mapBean)
                .collect(toList());
    }

    static boolean notIndexFile(HippoBean bean) {
        return !INDEX.equals(bean.getName());
    }

    // do not list certain folders at the root (e.g. footer, adminstration)
    static boolean excluded(HippoBean bean, HippoFolderBean folder, HippoBean baseBean) {
        return folder.equals(baseBean) &&  EXCLUDED.contains(bean.getName());
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
        return bean.getBean(INDEX);
    }
}