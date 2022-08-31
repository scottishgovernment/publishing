package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import scot.mygov.publishing.beans.Mirror;

import java.util.*;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

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
 */
public class CategoryComponent extends EssentialsContentComponent {

    static final String INDEX = "index";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        setCategoryAttributes(request, response);
    }

    static void setCategoryAttributes(HstRequest request, HstResponse response) {

        if (hasContentBean(request)) {
            HippoFolderBean folder = getChildrenFolder(request);
            request.setAttribute("children", getChildren(folder));
        }
    }

    static HippoFolderBean getChildrenFolder(HstRequest request) {
        HippoBean bean = getDocumentBean(request);
        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
        HippoFolderBean folder = (HippoFolderBean) bean.getParentBean();
        // special case for the home page. If the base bean is the folder this category lives in, then
        // use the "browse" folder to get the children from
        return baseBean.isSelf(folder)
            ? browseFolder(folder)
            : folder;
    }

    static HippoFolderBean browseFolder(HippoFolderBean folder) {
        return folder.getFolders().stream()
                .filter(CategoryComponent::isBrowseFolder)
                .findFirst()
                .orElse(folder);
    }

    static boolean isBrowseFolder(HippoFolderBean folder) {
        return "browse".equals(folder.getName());
    }

    static HippoBean getDocumentBean(HstRequest request) {
        HippoBean document = request.getRequestContext().getContentBean();
        return document instanceof Mirror ? mirroredDocument(document) : document;
    }

    static HippoBean mirroredDocument(HippoBean document) {
        Mirror mirror = (Mirror) document;
        return mirror.getDocument();
    }

    static boolean hasContentBean(HstRequest request) {
        HippoBean bean = request.getRequestContext().getContentBean();
        return bean != null && !bean.isHippoFolderBean();
    }

    static List<Wrapper> getChildren(HippoFolderBean folder) {
        return folder
                .getChildBeans(HippoBean.class)
                .stream()
                .filter(CategoryComponent::notIndexFile)
                .map(CategoryComponent::mapFolder)
                .filter(Objects::nonNull)
                .filter(CategoryComponent::includeInParent)
                .map(CategoryComponent::wrap)
                .filter(Wrapper::containsNonNullBean)
                .collect(toList());
    }

    static boolean includeInParent(HippoBean bean) {
        return bean.getSingleProperty("publishing:showInParent", true);
    }

    static boolean notIndexFile(HippoBean bean) {
        return !INDEX.equals(bean.getName());
    }

    static Wrapper wrap(HippoBean bean) {
        return new Wrapper(mapMirror(bean));
    }

    /**
     * Replace any mirrors with the document they point at
     */
    private static HippoBean mapMirror(HippoBean bean) {
        return bean instanceof Mirror
                ? getDocumentFromMirror(bean)
                : bean;
    }

    /**
     * Map the bean to use for this bean.  If it is a folder then return the index file, otherwise just use this bean.
     */
    static HippoBean mapFolder(HippoBean bean) {
        return bean.isHippoFolderBean() ? indexBean(bean) : bean;
    }

    static HippoBean indexBean(HippoBean bean) {
        return bean.getBean(INDEX);
    }

    static HippoBean getDocumentFromMirror(HippoBean bean) {
        Mirror mirror = (Mirror) bean;
        return mirror.getDocument();
    }

    public static class Wrapper {
        private HippoBean bean;

        Wrapper(HippoBean bean) {
            this.bean = bean;
        }

        public HippoBean getBean() {
            return bean;
        }

        boolean containsNonNullBean() {
            return nonNull(bean);
        }
    }
}