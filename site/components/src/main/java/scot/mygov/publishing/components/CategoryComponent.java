package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import scot.mygov.publishing.beans.Base;
import scot.mygov.publishing.beans.Mirror;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

/**
 * Component used to back category pages (including the home page).
 *
 * This component makes a list of its navigable children available as the "children" attribute. This list contains the
 * bean for any children, with the index bean being added in the place of any sub-categories.  This includes subcategories
 * and any articles etc.
 *
 * This will also resolve any "mirror" content items.  These are used to allow a content item to appear in more than
 * one category.
 *
 * The footer and administration folders are excluded.
 */
public class CategoryComponent extends EssentialsContentComponent {

    static final String INDEX = "index";

    static final Set<String> EXCLUDED_FOLDERS = new HashSet<>();

    static {
        // node names that should not be included on the home page
        Collections.addAll(EXCLUDED_FOLDERS, "administration", "site-furniture");
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
        setAttributeWithPredicate(request, "children", folder, baseBean, b -> true);
        setAttributeWithPredicate(request, "pinned", folder, baseBean, CategoryComponent::isPinned);
        setAttributeWithPredicate(request, "unpinned", folder, baseBean, CategoryComponent::notPinned);
    }

    static void setAttributeWithPredicate(
            HstRequest request,
            String attrib,
            HippoFolderBean folder,
            HippoBean baseBean,
            Predicate<HippoBean> include) {

        request.setAttribute(
                attrib,
                getChildren(folder, baseBean, include)
        );
    }

    static boolean hasContentBean(HstRequest request) {
        return request.getRequestContext().getContentBean() != null;
    }

    static List<HippoBean> getChildren(HippoFolderBean folder, HippoBean baseBean, Predicate<HippoBean> include) {

        return folder
                .getChildBeans(HippoBean.class)
                .stream()
                .filter(CategoryComponent::notIndexFile)
                .filter(node -> !isExcluded(node, folder, baseBean))
                .map(CategoryComponent::mapFolder)
                .filter(include)
                .map(CategoryComponent::mapMirror)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    static boolean notIndexFile(HippoBean bean) {
        return !INDEX.equals(bean.getName());
    }

    // do not list certain folders at the root (e.g. footer, administration)
    static boolean isExcluded(HippoBean bean, HippoFolderBean folder, HippoBean baseBean) {
        return folder.equals(baseBean) &&  EXCLUDED_FOLDERS.contains(bean.getName());
    }

    static boolean isPinned(HippoBean bean) {
        if (bean instanceof Base) {
            return ((Base) bean).getPinned();
        }

        if (bean instanceof Mirror) {
            return ((Mirror) bean).getPinned();
        }

        return false;
    }

    static boolean notPinned(HippoBean bean) {
        return !isPinned(bean);
    }

    /**
     * Map the bean to use for this lint.  If it is a fodler then return the index file, otherwise just use this bean.
     */
    static HippoBean mapFolder(HippoBean bean) {
        return bean.isHippoFolderBean()
                ? indexBean(bean)
                : bean;
    }

    static HippoBean indexBean(HippoBean bean) {
        return bean.getBean(INDEX);
    }

    /**
     * Replace any mirrors with the document they point at
     */
    static HippoBean mapMirror(HippoBean bean) {
        return bean instanceof Mirror
                ? getDocumentFromMirror(bean)
                : bean;
    }

    static HippoBean getDocumentFromMirror(HippoBean bean) {
        Mirror mirror = (Mirror) bean;
        return mirror.getDocument();
    }

}