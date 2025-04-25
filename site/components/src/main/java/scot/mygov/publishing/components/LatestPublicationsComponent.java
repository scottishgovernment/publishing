package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.beans.News;

@ParametersInfo(type = LatestPublicationsInfo.class)
public class LatestPublicationsComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(LatestPublicationsComponent.class);

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
        LatestPublicationsInfo paramInfo = getComponentParametersInfo(request);
        populate(baseBean, request, count(paramInfo));
        request.setAttribute("removebottompadding", paramInfo.getRemoveBottomPadding());
    }

    int count(LatestPublicationsInfo paramInfo) {
        try {
            return Integer.parseInt(paramInfo.getCount());
        } catch (NumberFormatException e) {
            LOG.error("Invalid card count: {}, defaulting to 3", paramInfo.getCount());
            return 3;
        }
    }

    void populate(HippoBean base, HstRequest request, int limit) {
        try {
            doPopulate(base, request, limit);
        } catch (QueryException e) {
            LOG.warn("Unable to get news", e);
        }
    }

    void doPopulate(HippoBean base, HstRequest request, int limit) throws QueryException {
        HstQuery query = query(base, limit);
        request.setAttribute("publications", query.execute().getHippoBeans());
        request.setAttribute("count", limit);
    }

    boolean hasImage(HippoBean bean) {
        News news = (News) bean;
        return news.getImage() != null;
    }

    HstQuery query(HippoBean base, int limit) {
        return HstQueryBuilder.create(base)
                .ofPrimaryTypes("publishing:Publication")
                .orderBy(HstQueryBuilder.Order.DESC, "publishing:publicationDate")
                .limit(limit)
                .build();
    }

}
