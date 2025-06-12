package scot.mygov.publishing.components;

import org.apache.commons.collections4.ListUtils;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.site.HstServices;
import org.onehippo.cms7.crisp.api.broker.ResourceServiceBroker;
import org.onehippo.cms7.crisp.api.resource.Resource;
import org.onehippo.cms7.crisp.api.resource.ResourceBeanMapper;
import org.onehippo.cms7.crisp.hst.module.CrispHstServices;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.onehippo.cms7.essentials.components.EssentialsDocumentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.components.eventbright.Event;
import scot.mygov.publishing.components.eventbright.EventbrightResults;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EventsComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(EventsComponent.class);

    @Override
    public void doBeforeRender(HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        HippoBean config = eventbrightConfig(request);

        if (config != null) {
            ResourceServiceBroker broker = CrispHstServices.getDefaultResourceServiceBroker(HstServices.getComponentManager());
            Map<String, Object> params = Collections.singletonMap("organisation", config.getSingleProperty("publishing:organisation"));
            Resource results = broker.resolve("eventbright", "/v3/organizations/{organisation}/events/?status=all", params);
            ResourceBeanMapper resourceBeanMapper = broker.getResourceBeanMapper("eventbright");
            EventbrightResults eventbrightResults = (EventbrightResults) resourceBeanMapper.map(results, EventbrightResults.class);
            List<Event> events = eventbrightResults.getEvents();
            List<List<Event>> partitioned = ListUtils.partition(events, 3);

            for (Event e : eventbrightResults.getEvents()) {
                LOG.info(e.getName().getText());
            }
            request.setAttribute("events", partitioned);
            request.setAttribute("organisation", config.getSingleProperty("publishing:organisation"));
        } else {
            request.setAttribute("events", Collections.emptyList());
        }

    }

    HippoBean eventbrightConfig(HstRequest request) {
        HippoBean site = request.getRequestContext().getSiteContentBaseBean();
        return site.getBean("administration/eventbright");
    }

}