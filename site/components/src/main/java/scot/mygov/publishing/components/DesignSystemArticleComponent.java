package scot.mygov.publishing.components;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.breadcrumb.om.BreadcrumbItem;
import scot.mygov.publishing.beans.Dsarticle;
import scot.mygov.publishing.beans.UpdateHistory;

import java.util.*;

public class DesignSystemArticleComponent extends ArticleComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        if (!hasContentBean(request)) {
            return;
        }

        HippoBean contentBean = request.getRequestContext().getContentBean();
        request.setAttribute("isNew", isNew(contentBean));
        request.setAttribute("type", type(request, contentBean));
        request.setAttribute("date", date(contentBean));

        request.setAttribute("format", "Article");
        HippoBean topLevelAncestor = topLevelAncestor(contentBean, request.getRequestContext().getSiteContentBaseBean());
        String topLevelTitle = title(topLevelAncestor);
        request.setAttribute("format", topLevelTitle);

        if (StringUtils.equalsAny(topLevelTitle, "Guidance")) {
            HippoFolderBean parent = (HippoFolderBean) contentBean.getParentBean();
            HippoBean seriesLink = parent.getBean("index");
            String seriesTitle = title(seriesLink);
            String title = title(contentBean);
            if (!StringUtils.equalsAny(seriesTitle, title, topLevelTitle)) {
                request.setAttribute("seriesLink", seriesLink);
            }
        }
    }

    Date date(HippoBean bean) {
        Dsarticle dsarticle = (Dsarticle) bean;
        List<UpdateHistory> updateHistory = dsarticle.getUpdateHistory();
        if (updateHistory.isEmpty()) {
            Calendar publishedDate = dsarticle.getSingleProperty("hippostdpubwf:publicationDate");
            return publishedDate.getTime();
        }
        Collections.sort(updateHistory, Comparator.comparing(UpdateHistory::getLastUpdated));
        return updateHistory.get(0).getLastUpdated().getTime();
    }

    String title(HippoBean bean) {
        return bean.getSingleProperty("publishing:title");
    }

    HippoBean topLevelAncestor(HippoBean contentBean, HippoBean baseBean) {
        // get the parents until we reach a folder whose parent is the site base bean ... then get the index
        HippoBean bean = contentBean;
        while (!isTopLevelParent(bean, baseBean)) {
            bean = bean.getParentBean();
        }
        return bean.getBean("index");
    }

    boolean isTopLevelParent(HippoBean contentBean, HippoBean baseBean) {
        return contentBean.getParentBean().isSelf(baseBean);
    }

    boolean isNew(HippoBean bean) {
        Dsarticle dsarticle = (Dsarticle) bean;
        List<UpdateHistory> updateHistory = dsarticle.getUpdateHistory();
        if (updateHistory.isEmpty()) {
            return false;
        }
        Collections.sort(updateHistory, Comparator.comparing(UpdateHistory::getLastUpdated));
        UpdateHistory oldestUpdate = updateHistory.get(0);
        Calendar firstPublished = oldestUpdate.getLastUpdated();
        return firstPublished.after(threeMonthsAgo());
    }

    Calendar threeMonthsAgo() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -3);
        return c;
    }

    String type(HstRequest request, HippoBean contentBean) {
        List<BreadcrumbItem> breadcrumbs = BreadcrumbComponent.constructBreadcrumb(request, contentBean);
        return breadcrumbs.size() > 1
                ? breadcrumbs.get(1).title
                : "";
    }
}
