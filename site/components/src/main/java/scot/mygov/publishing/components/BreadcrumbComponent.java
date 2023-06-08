package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.onehippo.forge.breadcrumb.om.BreadcrumbItem;
import scot.mygov.publishing.beans.GuidePage;
import scot.mygov.publishing.beans.Home;

import java.util.ArrayList;
import java.util.List;

import static scot.mygov.publishing.components.CategoryComponent.INDEX;
import static java.util.Collections.emptyList;
import static scot.mygov.publishing.components.CategoryComponent.indexBean;

/**
 * Construct a list of BreadcrumbItem objects for the page being requested.
 */
public class BreadcrumbComponent extends EssentialsContentComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HippoBean contentBean = CategoryComponent.getDocumentBean(request);
        HstRequestContext context = request.getRequestContext();

        // special case for guide pages: we do not want the page to be a part of the breadcrumb
        if (contentBean instanceof GuidePage) {
            contentBean = contentBean.getParentBean().getBean("index");
        }

        List<BreadcrumbItem> breadcrumbs = constructBreadcrumb(request, contentBean);

        // ensure that there is always a link to the home page at the start of the breadcrumbs
        if (breadcrumbs.isEmpty()) {
            breadcrumbs.add(breadcrumbItem(context.getSiteContentBaseBean(), context, "Home"));
        }
        // make sure the
        request.setAttribute("breadcrumbs", breadcrumbs);
        request.setAttribute("documentBreadcrumbItem", breadcrumbItem(contentBean, context));
    }

    static List<BreadcrumbItem> constructBreadcrumb(HstRequest request, HippoBean contentBean) {
        if (request.getRequestContext().getContentBean() == null) {
            return emptyList();
        }

        if (request.getRequestContext().getContentBean() instanceof Home) {
            return emptyList();
        }

        HstRequestContext context = request.getRequestContext();
        HippoBean baseBean = context.getSiteContentBaseBean();
        HippoBean startBean = startBean(contentBean);
        return breadcrumbs(startBean, baseBean, context);
    }

    static HippoBean startBean(HippoBean contentBean) {
        if (contentBean.isHippoFolderBean()) {
            contentBean = contentBean.getBean("index");
        }

        // determine the bean to start with - different for category index files than for articles etc.
        return INDEX.equals(contentBean.getName())
                ? contentBean.getParentBean().getParentBean()
                : contentBean.getParentBean();
    }

    static List<BreadcrumbItem> breadcrumbs(HippoBean bean, HippoBean baseBean, HstRequestContext context) {
        boolean isBaseBean = isBaseBean(bean, baseBean);
        List<BreadcrumbItem> breadcrumbs = isBaseBean
                ? new ArrayList<>()
                : breadcrumbs(bean.getParentBean(), baseBean, context);
        HippoBean linkBean = indexBean(bean);

        // we do this to handle structural folders (for example the footer folder)
        // these folders do not contain an index file.
        if (linkBean != null) {
            breadcrumbs.add(breadcrumbItem(linkBean, context));
        } else if (isBaseBean) {
            // this is deal with the difference between mygov which has a home page in the root folder, and
            // sites such as designmanual whose home page is content block based.
            breadcrumbs.add(breadcrumbItem(baseBean, context));
        }

        return breadcrumbs;
    }

    static boolean isBaseBean(HippoBean bean, HippoBean baseBean) {
        return bean.getIdentifier().equals(baseBean.getIdentifier());
    }

    static BreadcrumbItem breadcrumbItem(HippoBean bean, HstRequestContext context) {
        return breadcrumbItem(bean, context, bean.getSingleProperty("publishing:title"));
    }

    static BreadcrumbItem breadcrumbItem(HippoBean bean, HstRequestContext context, String title) {
        HstLinkCreator linkCreator = context.getHstLinkCreator();
        HstLink link = linkCreator.create(bean, context);
        return new BreadcrumbItem(link, title);
    }
}