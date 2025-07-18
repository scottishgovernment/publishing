package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;
import scot.mygov.publishing.beans.Dsarticle;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class SideNavigationComponent extends CommonComponent {

    private static final String INDEX = "index";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HstRequestContext context = request.getRequestContext();
        HippoBean bean = context.getContentBean();
        request.setModel(REQUEST_ATTR_DOCUMENT, bean);
        populateNavigation(request);
    }

    void populateNavigation(HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        Optional<HippoFolderBean> topLevelFolder = topLevelFolder(context);
        NavigationItem item = new NavigationItem();
        if (topLevelFolder.isPresent()) {
            populateNavigationItem(context, topLevelFolder.get(), item);
        }
        request.setAttribute("navigation", item);
    }

    Optional<HippoFolderBean> topLevelFolder(HstRequestContext context) {
        HippoFolderBean root = context.getSiteContentBaseBean().getBean("browse");
        HippoBean contentBean = context.getContentBean();
        return root.getChildBeans(HippoFolderBean.class)
                .stream()
                .filter(folder -> folder.isAncestor(contentBean))
                .findFirst();
    }


    void populateNavigationItem(HstRequestContext context, HippoFolderBean folder, NavigationItem item) {
        List<HippoBean> children = folder.getChildBeans(HippoBean.class).stream().filter(this::notIndex).collect(toList());
        for (HippoBean child : children) {
            populateNavigationItemChild(context, child, item);
        }
    }

    void populateNavigationItemChild(HstRequestContext context, HippoBean child, NavigationItem item) {
        if (child instanceof Dsarticle) {
            NavigationItem articleItem = navigationItem(context, child);
            item.getChildren().add(articleItem);
            return;
        }

        if (child.isHippoFolderBean()) {
            populateNavigationItemChildFolder(context, (HippoFolderBean) child, item);
        }
    }

    void populateNavigationItemChildFolder(HstRequestContext context, HippoFolderBean folder, NavigationItem item) {
        if (!hasIndex(folder)) {
            return;
        }

        HippoBean index = folder.getBean(INDEX);
        NavigationItem folderItem = navigationItem(context, index);
        item.getChildren().add(folderItem);

        if (folder.isAncestor(context.getContentBean())) {
            populateNavigationItem(context, folder, folderItem);
        }
    }

    boolean hasIndex(HippoFolderBean folderBean) {
        HippoBean index = folderBean.getBean(INDEX);
        return index != null;
    }

    boolean notIndex(HippoBean bean) {
        return !INDEX.equals(bean.getName());
    }

    NavigationItem navigationItem(HstRequestContext context, HippoBean bean) {
        NavigationItem childItem = new NavigationItem();
        childItem.setLink(context.getHstLinkCreator().create(bean, context));
        childItem.setTitle(bean.getSingleProperty("publishing:title"));
        childItem.setCurrentItem(bean.isSelf(context.getContentBean()));
        return childItem;
    }
}
