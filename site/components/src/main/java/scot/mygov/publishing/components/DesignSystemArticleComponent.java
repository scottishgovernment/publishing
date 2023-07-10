package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.breadcrumb.om.BreadcrumbItem;
import scot.mygov.publishing.beans.Dsarticle;
import scot.mygov.publishing.beans.UpdateHistory;

import java.util.Calendar;
import java.util.List;

public class DesignSystemArticleComponent extends ArticleComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HippoBean contentBean = request.getRequestContext().getContentBean();
        request.setAttribute("isNew", isNew(contentBean));
        request.setAttribute("type", type(request, contentBean));
    }

    boolean isNew(HippoBean bean) {
        Dsarticle dsarticle = (Dsarticle) bean;
        List<UpdateHistory> updateHistory = dsarticle.getUpdateHistory();
        if (updateHistory.isEmpty()) {
            return false;
        }
        UpdateHistory mostRecent = updateHistory.get(0);
        Calendar lastUpdated = mostRecent.getLastUpdated();
        return lastUpdated.after(threeMonthsAgo());
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
