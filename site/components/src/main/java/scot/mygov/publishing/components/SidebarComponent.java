package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.content.beans.standard.HippoDocumentBean;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.beans.Step;
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
            Step currentStep = findCurrentStep(contentBean, result);
            request.setAttribute("currentStep", currentStep);
            request.setAttribute("stepbysteps", result.getHippoBeans());
        } catch (QueryException e) {
            LOG.error("Query error trying to find step by sides guides for {}", contentBean.getPath(), e);
        }
    }

    Step findCurrentStep(HippoDocumentBean contentBean, HstQueryResult result) {
        HippoBeanIterator it = result.getHippoBeans();
        while (it.hasNext()) {
            StepByStepGuide stepByStepGuide = (StepByStepGuide) it.nextHippoBean();
            Step matchingStep = findCurrentStep(contentBean, stepByStepGuide);
            if (matchingStep != null) {
                return matchingStep;
            }
        }
        return null;
    }

    Step findCurrentStep(HippoDocumentBean contentBean, StepByStepGuide stepByStepGuide) {
        for (Step step : stepByStepGuide.getSteps()) {
            if (mentionsNode(step, contentBean)) {
                return step;
            }
        }
        return null;
    }

    boolean mentionsNode(Step step, HippoDocumentBean bean) {
        HippoHtml html = step.getContent();
        String handleUUID = bean.getCanonicalHandleUUID();
        return html.<HippoBean>getChildBeans("hippo:facetselect")
                .stream()
                .anyMatch(facet ->  facet.<String>getSingleProperty("hippo:docbase").equals(handleUUID));
    }
}
