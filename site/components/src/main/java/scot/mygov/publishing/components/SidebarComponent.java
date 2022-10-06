package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.beans.StepByStepGuide;

import static org.hippoecm.hst.util.ContentBeanUtils.createIncomingBeansQuery;

public class SidebarComponent extends EssentialsContentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SidebarComponent.class);

    private static final String LINK_PATH = "publishing:steps/publishing:content/*/@hippo:docbase";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        HstRequestContext context = request.getRequestContext();
        HippoDocumentBean contentBean = (HippoDocumentBean) context.getContentBean();

        HippoBean scope = context.getSiteContentBaseBean();
        request.setModel(REQUEST_ATTR_DOCUMENT, contentBean);
        try {
            HstQuery query = createIncomingBeansQuery(contentBean, scope, LINK_PATH, StepByStepGuide.class, false);
            HstQueryResult result = query.execute();
            request.setAttribute("stepbysteps", result.getHippoBeans());
        } catch (QueryException e) {
            LOG.error("Query error trying to find step by sides guides for {}", contentBean.getPath(), e);
        }
    }
}
