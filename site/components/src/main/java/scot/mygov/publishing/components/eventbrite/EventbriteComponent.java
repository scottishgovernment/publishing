package scot.mygov.publishing.components.eventbrite;

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
import scot.mygov.publishing.components.eventbrite.model.EventbriteEvent;
import scot.mygov.publishing.components.eventbrite.model.EventbriteResults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.isBlank;

@ParametersInfo(type = EventsComponentInfo.class)
public class EventbriteComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(EventbriteComponent.class);

    private static final String EVENTS = "events";

    private static final String ORGANIZATION_ID = "organizationId";

    private static final String TOTAL = "total";

    private static final String TITLE = "title";

    private static final String SHOW_IMAGES = "showImages";

    private static final String ERROR_STATE = "errorState";

    private static final String URL_TEMPLATE = "/v3/organizations/{org}/events/?order_by=start_asc&page_size={count}&time_filter=current_future&status=live";

    @Override
    public void doBeforeRender(HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);
        EventsComponentInfo paramInfo = getComponentParametersInfo(request);
        request.setAttribute(SHOW_IMAGES, paramInfo.getShowImages());
        request.setAttribute(TITLE, paramInfo.getTitle());
        request.setAttribute(ORGANIZATION_ID, paramInfo.getOrganisationId());
        if (isBlank(paramInfo.getOrganisationId())) {
            populateEmptyRequest(request, false);
            return;
        }

        try {
            ResourceServiceBroker broker = CrispHstServices.getDefaultResourceServiceBroker(HstServices.getComponentManager());
            Resource results = broker.resolve("eventbrite", URL_TEMPLATE, paramMap(paramInfo));
            ResourceBeanMapper resourceBeanMapper = broker.getResourceBeanMapper("eventbrite");
            EventbriteResults eventbriteResults = resourceBeanMapper.map(results, EventbriteResults.class);
            populateRequest(request, eventbriteResults);
            EventbriteStatusTracker.recordSuccess();
        } catch (ResourceException e) {
            LOG.error("Failed to fetch events", e);
            EventbriteStatusTracker.recordError(e);
            populateEmptyRequest(request, true);
        }
    }

    Map<String, Object> paramMap(EventsComponentInfo paramInfo) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", paramInfo.getCount());
        params.put("org", paramInfo.getOrganisationId());
        return params;
    }

    void populateEmptyRequest(HstRequest request, boolean error) {
        request.setAttribute(EVENTS, emptyList());
        request.setAttribute(TOTAL, 0);
        request.setAttribute(ERROR_STATE, error);
    }

    void populateRequest(HstRequest request, EventbriteResults eventbriteResults) {
        List<Event> events = eventbriteResults.getEvents().stream().map(this::toEvent).collect(Collectors.toList());
        request.setAttribute(EVENTS, events);
        request.setAttribute(TOTAL, eventbriteResults.getPagination().getObjectCount());
        request.setAttribute(ERROR_STATE, false);
    }

    Event toEvent(EventbriteEvent e) {
        Event event = new Event();
        event.setTitle(e.getName().getText());
        event.setSummary(e.getDescription().getText());
        event.setUrl(e.getUrl());
        event.setImageurl(e.getLogo().getUrl());
        event.setDateTime(e.getStart().getDateTime());
        return event;
    }
}