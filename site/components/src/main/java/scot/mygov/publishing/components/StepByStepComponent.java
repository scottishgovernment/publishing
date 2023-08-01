package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.*;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.beans.GuidePage;
import scot.mygov.publishing.beans.Step;
import scot.mygov.publishing.beans.StepByStepGuide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.hippoecm.hst.util.ContentBeanUtils.createIncomingBeansQuery;
import static scot.mygov.publishing.components.CategoryComponent.hasContentBean;


public class StepByStepComponent extends EssentialsContentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(StepByStepComponent.class);

    private static final String LINK_PATH = "publishing:steps/publishing:content/*/@hippo:docbase";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        if (!hasContentBean(request)) {
            return;
        }
        HstRequestContext context = request.getRequestContext();
        HippoDocumentBean contentBean = (HippoDocumentBean) context.getContentBean();
        request.setModel(REQUEST_ATTR_DOCUMENT, contentBean);

        List<StepByStepWrapper> wrappers = wrappersForBean(request, contentBean);
        if (contentBean instanceof GuidePage) {
            addGuideStepBySteps(contentBean, request, wrappers);
        }
        request.setAttribute("stepbysteps", wrappers);
    }

    List<StepByStepWrapper> wrappersForBean(HstRequest request, HippoDocumentBean bean) {
        try {
            HippoBean scope = request.getRequestContext().getSiteContentBaseBean();
            HstQuery query = createIncomingBeansQuery(bean, scope, LINK_PATH, StepByStepGuide.class, false);
            HstQueryResult result = query.execute();
            return wrappersForStepByStepBuidResults(result.getHippoBeans(), bean);
        } catch (QueryException e) {
            LOG.error("Query error trying to find step by sides guides for {}", bean.getPath(), e);
            return Collections.emptyList();
        }
    }

    List<StepByStepWrapper> wrappersForStepByStepBuidResults(HippoBeanIterator it, HippoDocumentBean bean) {
        List<StepByStepWrapper> wrappers = new ArrayList<>();
        while (it.hasNext()) {
            StepByStepGuide stepByStepGuide = (StepByStepGuide) it.nextHippoBean();
            StepByStepWrapper wrapper = wrapper(bean, stepByStepGuide);
            wrappers.add(wrapper);
        }
        return wrappers;
    }

    StepByStepWrapper wrapper(HippoDocumentBean bean, StepByStepGuide stepByStepGuide) {
        Step currentStep = findCurrentStep(bean, stepByStepGuide);
        StepByStepWrapper wrapper = new StepByStepWrapper();
        wrapper.setStepByStepGuide(stepByStepGuide);
        wrapper.setCurrentStep(currentStep);
        return wrapper;
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

    void addGuideStepBySteps(HippoDocumentBean contentBean, HstRequest request, List<StepByStepWrapper> wrappers) {
        HippoFolderBean folder = (HippoFolderBean) contentBean.getParentBean();
        HippoDocumentBean guide = folder.getBean("index");
        List<StepByStepWrapper> wrappersForGuide = wrappersForBean(request, guide);
        Set<String> stepByStepGuidIds = wrappers.stream().map(StepByStepWrapper::id).collect(toSet());
        for (StepByStepWrapper wrapper : wrappersForGuide) {
            if (!stepByStepGuidIds.contains(wrapper.id())) {
                wrappers.add(wrapper);
            }
        }
    }

    public static class StepByStepWrapper {

        private StepByStepGuide stepByStepGuide;

        private Step currentStep;

        public StepByStepGuide getStepByStepGuide() {
            return stepByStepGuide;
        }

        public void setStepByStepGuide(StepByStepGuide stepByStepGuide) {
            this.stepByStepGuide = stepByStepGuide;
        }

        public Step getCurrentStep() {
            return currentStep;
        }

        public void setCurrentStep(Step currentStep) {
            this.currentStep = currentStep;
        }

        public String id() {
            return stepByStepGuide.getIdentifier();
        }
    }
}
