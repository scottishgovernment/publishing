package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.manager.ObjectBeanManager;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Item;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;


/**
 * setup sitemap for manual, pages (3 levels deep) , documents
 * make manual component that always makes availabkle
 *  - navigation
 *  - the manual / publication
 *
 * -setup basic templates that juts pout stuff on screen, maybe borrow design system nav templates?
 */

public class PublicationComponent extends NewsComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationComponent.class);

    private static final String INDEX = "index";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        Optional<HippoFolderBean> folder = publicationFolder(request, request.getRequestContext().getContentBean());
        if (folder.isPresent()) {
            HippoBean publication = folder.get().getBean(INDEX);
            HippoBean documents = folder.get().getBean("documents");
            addTopics(request, publication);
            request.setAttribute("publication", publication);
            request.setAttribute("documents", documents);
            populateNavigation(request, folder.get());
        }
    }

    public static Optional<HippoFolderBean> publicationFolder(HstRequest request, HippoBean bean) {
        try {
            // this assumes that publications are in the publicaitons folde with year and month folders
            Item ancestorNode = bean.getNode().getAncestor(7);
            ObjectBeanManager obm = request.getRequestContext().getObjectBeanManager();
            HippoFolderBean ancestor = (HippoFolderBean) obm.getObject(ancestorNode.getPath());
            return Optional.of(ancestor);
        } catch (RepositoryException e) {
            LOG.error("unable to find publication node for {}", request.getPathInfo(), e);
            return Optional.empty();
        } catch (ObjectBeanManagerException e) {
            LOG.error("exception mapping publication folder for {}", request.getPathInfo(), e);
            return Optional.empty();
        }
    }

    void populateNavigation(HstRequest request, HippoFolderBean publicationFolder) {

        HippoFolderBean pages = publicationFolder.getBean("pages", HippoFolderBean.class);
        if (pages == null) {
            LOG.error("No pages folder for {}", publicationFolder.getPath());
            return;
        }

        NavigationItem item = new NavigationItem();
        List<NavigationItem> allItems = new ArrayList<>();
        populateNavigationItem(request.getRequestContext(), pages, item, allItems);
        Optional<NavigationItem> current = allItems.stream().filter(NavigationItem::isCurrentItem).findFirst();
        if (current.isPresent()) {
            int currentIndex = allItems.indexOf(current.get());
            if (currentIndex != 0) {
                request.setAttribute("prev", allItems.get(currentIndex - 1));
            }
            request.setAttribute("curr", allItems.get(currentIndex));
            if (currentIndex != allItems.size() - 1) {
                request.setAttribute("next", allItems.get(currentIndex + 1));
            }
        }
        // determine prev next, linearize the tree
        allItems.add(item);

        // if current is a sub-sub page then we want to populate the parent title
        request.setAttribute("parentTitle", item.findParentTitleOfCurrent());
        request.setAttribute("navigation", item);
    }

    void populateNavigationItem(HstRequestContext context, HippoFolderBean folder, NavigationItem item, List<NavigationItem> allItems) {
        List<HippoBean> children = folder.getChildBeans(HippoBean.class).stream().filter(this::notIndex).collect(toList());
        for (HippoBean child : children) {
            populateNavigationItemChild(context, child, item, allItems);
        }
    }

    void populateNavigationItemChild(HstRequestContext context, HippoBean child, NavigationItem item, List<NavigationItem> allItems) {
        if (!child.isHippoFolderBean()) {
            NavigationItem articleItem = navigationItem(context, child);
            allItems.add(articleItem);
            item.getChildren().add(articleItem);
            return;
        }
        populateNavigationItemChildFolder(context, (HippoFolderBean) child, item, allItems);
    }

    void populateNavigationItemChildFolder(HstRequestContext context, HippoFolderBean folder, NavigationItem item,  List<NavigationItem> allItems) {
        if (!hasIndex(folder)) {
            return;
        }

        HippoBean index = folder.getBean(INDEX);
        NavigationItem folderItem = navigationItem(context, index);
        allItems.add(folderItem);
        item.getChildren().add(folderItem);
        populateNavigationItem(context, folder, folderItem, allItems);
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
        HstLink link = context.getHstLinkCreator().create(bean, context);
        childItem.setLink(link);
        childItem.setTitle(bean.getSingleProperty("publishing:title"));
        childItem.setCurrentItem(bean.isSelf(context.getContentBean()));
        return childItem;
    }
}
