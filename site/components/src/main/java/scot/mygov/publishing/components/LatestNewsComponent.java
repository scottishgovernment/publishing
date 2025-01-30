package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.beans.News;

import java.util.ArrayList;
import java.util.List;

@ParametersInfo(type = LatestNewsInfo.class)
public class LatestNewsComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(LatestNewsComponent.class);

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
        LatestNewsInfo paramInfo = getComponentParametersInfo(request);
        populateNews(baseBean, request, count(paramInfo));

        request.setAttribute("allowImages", paramInfo.getAllowImages());
        request.setAttribute("neutrallinks", paramInfo.getNeutralLinks());
        request.setAttribute("removebottompadding", paramInfo.getRemoveBottomPadding());
    }

    int count(LatestNewsInfo paramInfo) {
        try {
            return Integer.parseInt(paramInfo.getCount());
        } catch (NumberFormatException e) {
            LOG.error("Invalid card count: {}, defaulting to 3", paramInfo.getCount());
            return 3;
        }
    }

    void populateNews(HippoBean base, HstRequest request, int limit) {
        try {
            doPopulateNews(base, request, limit);
        } catch (QueryException e) {
            LOG.warn("Unable to get news", e);
        }
    }

    void doPopulateNews(HippoBean base, HstRequest request, int limit) throws QueryException {
        HstQuery query = query(base, limit);
        HippoBeanIterator it = query.execute().getHippoBeans();
        List<HippoBean> beans = new ArrayList<>();
        boolean showImages = true;
        while (it.hasNext()) {
            HippoBean bean = it.nextHippoBean();
            if (!hasImage(bean)) {
                showImages = false;
            }
            beans.add(bean);
        }
        request.setAttribute("news", beans);
        request.setAttribute("showImages", showImages);
        request.setAttribute("count", limit);
    }

    boolean hasImage(HippoBean bean) {
        News news = (News) bean;
        return news.getImage() != null;
    }

    HstQuery query(HippoBean base, int limit) {
        return HstQueryBuilder.create(base)
                .ofPrimaryTypes("publishing:News")
                .orderBy(HstQueryBuilder.Order.DESC, "publishing:publicationDate")
                .limit(limit)
                .build();
    }

}
