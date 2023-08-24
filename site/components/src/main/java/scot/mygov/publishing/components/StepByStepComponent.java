package scot.mygov.publishing.components;

import org.apache.commons.lang3.StringUtils;
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

import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.sort;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hippoecm.hst.util.ContentBeanUtils.createIncomingBeansQuery;

public class StepByStepComponent extends EssentialsContentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(StepByStepComponent.class);

    private static final String LINK_PATH = "publishing:steps/publishing:content/*/@hippo:docbase";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        HstRequestContext context = request.getRequestContext();
        HippoDocumentBean contentBean = (HippoDocumentBean) context.getContentBean();
        request.setModel(REQUEST_ATTR_DOCUMENT, contentBean);

        List<StepByStepWrapper> wrappers = getStepByStepWrappers(request, contentBean);
        List<StepByStepWrapper> others = wrappers.size() == 1
                ? emptyList()
                : wrappers.stream().filter(w -> !w.isNavItem()).collect(toList());
        Optional<StepByStepWrapper> primaryStepByStep = wrappers.stream().filter(StepByStepWrapper::isNavItem).findFirst();
        request.setAttribute("stepBySteps", wrappers);
        request.setAttribute("otherStepByStepGuides", others);

        if (wrappers.size() == 1) {
            request.setAttribute("primaryStepByStep", wrappers.get(0));
        } else if (primaryStepByStep.isPresent()) {
            request.setAttribute("primaryStepByStep", primaryStepByStep.get());
        }
    }

    List<StepByStepWrapper> getStepByStepWrappers(HstRequest request, HippoDocumentBean contentBean) {
        String navParam = request.getParameter("step-by-step-nav");
        List<StepByStepWrapper> wrappers = wrappersForBean(request, contentBean, navParam);
        if (contentBean instanceof GuidePage) {
            addGuideStepBySteps(contentBean, request, wrappers, navParam);
        }
        sort(wrappers, comparing(sbs -> sbs.getStepByStepGuide().getTitle()));
        return wrappers;
    }

    List<StepByStepWrapper> wrappersForBean(HstRequest request, HippoDocumentBean bean, String navParam) {
        try {
            HippoBean scope = request.getRequestContext().getSiteContentBaseBean();
            HstQuery query = createIncomingBeansQuery(bean, scope, LINK_PATH, StepByStepGuide.class, false);
            HstQueryResult result = query.execute();
            return wrappersForStepByStepQueryResults(result.getHippoBeans(), bean, navParam);
        } catch (QueryException e) {
            LOG.error("Query error trying to find step by sides guides for {}", bean.getPath(), e);
            return emptyList();
        }
    }

    List<StepByStepWrapper> wrappersForStepByStepQueryResults(HippoBeanIterator it, HippoDocumentBean bean, String navParam) {
        List<StepByStepWrapper> wrappers = new ArrayList<>();
        while (it.hasNext()) {
            StepByStepGuide stepByStepGuide = (StepByStepGuide) it.nextHippoBean();
            StepByStepWrapper wrapper = wrapper(bean, stepByStepGuide, navParam);
            wrappers.add(wrapper);
        }
        return wrappers;
    }

    StepByStepWrapper wrapper(HippoDocumentBean bean, StepByStepGuide stepByStepGuide, String navParam) {
        Step currentStep = findCurrentStep(bean, stepByStepGuide);
        StepByStepWrapper wrapper = new StepByStepWrapper();
        wrapper.setStepByStepGuide(stepByStepGuide);
        wrapper.setCurrentStep(currentStep);
        if (StringUtils.equals(navParam, stepByStepGuide.getSlug())) {
            wrapper.setNavItem(true);
        }
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

    void addGuideStepBySteps(HippoDocumentBean contentBean, HstRequest request, List<StepByStepWrapper> wrappers, String nav) {
        HippoFolderBean folder = (HippoFolderBean) contentBean.getParentBean();
        HippoDocumentBean guide = folder.getBean("index");
        List<StepByStepWrapper> wrappersForGuide = wrappersForBean(request, guide, nav);
        Set<String> stepByStepGuidIds = wrappers.stream().map(StepByStepWrapper::id).collect(toSet());
        for (StepByStepWrapper wrapper : wrappersForGuide) {
            if (!stepByStepGuidIds.contains(wrapper.id())) {
                wrappers.add(wrapper);
            }
        }
    }

    public static class StepByStepWrapper {

        private boolean navItem = false;

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

        public boolean isNavItem() {
            return navItem;
        }

        public void setNavItem(boolean navItem) {
            this.navItem = navItem;
        }
    }
}
