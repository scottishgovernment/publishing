package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.channels.WebsiteInfo;

import javax.jcr.RepositoryException;

public class FeedbackComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FeedbackComponent.class);

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        request.setAttribute("layoutName", getLayoutName(request));
        request.setAttribute("isFeedbackEnabled", isFeedbackEnabled(request));
        request.setAttribute("feedbackDocument", feedbackDocument(request));
    }

    String getLayoutName(HstRequest request) {
        return request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getHstComponentConfiguration()
                .getName();
    }

    boolean isFeedbackEnabled(HstRequest request) {
        WebsiteInfo info = MountUtils.websiteInfo(request);
        if (!info.isFeedbackEnabled()) {
            return false;
        }
        HippoBean bean = request.getRequestContext().getContentBean();

        if (bean == null) {
            return false;
        }

        if (bean.isHippoFolderBean()) {
            return false;
        }
        request.setAttribute("isSEODocument", isSEODocument(bean));
        return bean.getSingleProperty("publishing:showFeedback", false);
    }

    boolean isSEODocument(HippoBean bean) {
        try {
            return bean.getNode().isNodeType("publishing:seo");
        } catch (RepositoryException e) {
            LOG.error("unable to get node type", e);
            return false;
        }
    }

    HippoBean feedbackDocument(HstRequest request) {
        HippoBean site = request.getRequestContext().getSiteContentBaseBean();
        return site.getBean("site-furniture/feedback");
    }
}
