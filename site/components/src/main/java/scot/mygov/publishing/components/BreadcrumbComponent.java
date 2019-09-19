package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.onehippo.forge.breadcrumb.om.BreadcrumbItem;

import java.util.ArrayList;
import java.util.List;

import static scot.mygov.publishing.components.CategoryComponent.indexBean;

/**
 * Construct a list of BreadcrumItem objects for the page being requested.
 */
public class BreadcrumbComponent extends EssentialsContentComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        List<BreadcrumbItem> breadcrumbs = constructBreadcrumb(request);
        request.setAttribute("breadcrumbs", breadcrumbs);
    }

    static List<BreadcrumbItem> constructBreadcrumb(HstRequest request) {
        HstRequestContext context = request.getRequestContext();
        HippoBean baseBean = context.getSiteContentBaseBean();
        HippoBean contentBean = context.getContentBean();
        return breadcrumbs(startBean(contentBean), baseBean, context);
    }

    static HippoBean startBean(HippoBean contentBean) {
        // determine the bean to start with - different for category index files than for articles etc.
        return contentBean.getName().equals("index")
                ? contentBean.getParentBean().getParentBean()
                : contentBean.getParentBean();
    }

    static List<BreadcrumbItem> breadcrumbs(HippoBean bean, HippoBean baseBean, HstRequestContext context) {
        List<BreadcrumbItem> breadcrumbs = isBaseBean(bean, baseBean)
                ? new ArrayList<>()
                : breadcrumbs(bean.getParentBean(), baseBean, context);
        breadcrumbs.add(breadcrumbItem(bean, context));
        return breadcrumbs;
    }

    static boolean isBaseBean(HippoBean bean, HippoBean baseBean) {
        return bean.getIdentifier().equals(baseBean.getIdentifier());
    }

    static BreadcrumbItem breadcrumbItem(HippoBean bean, HstRequestContext context) {
        HstLinkCreator linkCreator = context.getHstLinkCreator();
        HippoBean linkBean = indexBean(bean);
        HstLink link = linkCreator.create(linkBean, context);
        return new BreadcrumbItem(link, linkBean.getSingleProperty("publishing:title"));
    }

}