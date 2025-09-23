package scot.mygov.publishing.components;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.site.HstServices;
import org.onehippo.cms7.crisp.api.broker.ResourceServiceBroker;
import org.onehippo.cms7.crisp.api.resource.Resource;
import org.onehippo.cms7.crisp.api.resource.ResourceBeanMapper;
import org.onehippo.cms7.crisp.api.resource.ResourceException;
import org.onehippo.cms7.crisp.hst.module.CrispHstServices;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.components.eventbright.Event;
import scot.mygov.publishing.components.eventbright.EventbrightResults;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersInfo(type = EventsComponentInfo.class)
public class EventsComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(EventsComponent.class);

    @Override
    public void doBeforeRender(HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);
        EventsComponentInfo paramInfo = getComponentParametersInfo(request);

        if (StringUtils.isBlank(paramInfo.getOrganisationId())) {
            request.setAttribute("events", Collections.emptyList());
            request.setAttribute("organizerId", "");
            request.setAttribute("total", 0);
            return;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("count", paramInfo.getCount());
        params.put("organisation", paramInfo.getOrganisationId());
        ResourceServiceBroker broker = CrispHstServices.getDefaultResourceServiceBroker(HstServices.getComponentManager());

        try {
            Resource results = broker.resolve("eventbright", "/v3/organizations/{organisation}/events/?order_by=start_asc&page_size={count}", params);
            ResourceBeanMapper resourceBeanMapper = broker.getResourceBeanMapper("eventbright");
            EventbrightResults eventbrightResults = resourceBeanMapper.map(results, EventbrightResults.class);
            List<Event> events = eventbrightResults.getEvents();
            events.forEach(e -> e.setDateTimeString(e.getStart().getDateTime()));
            String organizerId = events.isEmpty() ? "" : events.get(0).getOrganizerId();
            request.setAttribute("events", events);
            request.setAttribute("organizerId", organizerId);
            request.setAttribute("total", eventbrightResults.getPagination().getObjectCount());
        } catch (ResourceException e) {
            LOG.error("Failed to fetch events", e);
            request.setAttribute("events", Collections.emptyList());
            request.setAttribute("organizerId", "");
            request.setAttribute("total", 0);
        }

    }

}