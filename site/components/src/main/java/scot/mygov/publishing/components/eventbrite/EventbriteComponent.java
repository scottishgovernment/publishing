package scot.mygov.publishing.components.eventbrite;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import jakarta.servlet.ServletContext;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.ComponentConfiguration;
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

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.isBlank;

/***
 * Eventbrite component,
 *
 */
@ParametersInfo(type = EventsComponentInfo.class)
public class EventbriteComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(EventbriteComponent.class);

    private static final String EVENTS = "events";

    private static final String ORGANIZER_ID = "organizerId";

    private static final String TOTAL = "total";

    private static final String TITLE = "title";

    private static final String SHOW_ALL_TEXT = "showAllText";

    private static final String SHOW_IMAGES = "showImages";

    private static final String ERROR_STATE = "errorState";

    private static final String URL_TEMPLATE = "/v3/organizations/{org}/events/?order_by=start_asc&page_size={count}&time_filter=current_future&status=live";

    CircuitBreaker circuitBreaker;

    @Override
    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) {
        super.init(servletContext, componentConfig);
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(8)
                .minimumNumberOfCalls(4)
                // Trip if 4/8 fail
                .failureRateThreshold(50)
                // FAIL-FAST: Trip if calls take longer than 2 seconds
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                .slowCallRateThreshold(50)
                // RECOVERY: Wait a minute for rate-limit reset
                .waitDurationInOpenState(Duration.ofMinutes(1))
                .permittedNumberOfCallsInHalfOpenState(3)
                // OPTIMIZATION: Don't fill logs with stack traces when circuit is open
                //.writableStackTraceEnabled(false)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        circuitBreaker = registry.circuitBreaker("eventbrite");
        circuitBreaker.getEventPublisher().onStateTransition(event -> LOG.info("STATE CHANGE: {}", event.getStateTransition()));
        circuitBreaker.getEventPublisher()
                .onSuccess(event -> LOG.info("Call succeeded through breaker."))
                .onError(event -> LOG.error("Call failed! Error: {}", event.getThrowable().getMessage()))
                .onIgnoredError(event -> LOG.error("An error occurred but it's configured to be ignored."));

    }

    @Override
    public void doBeforeRender(HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        EventsComponentInfo paramInfo = getComponentParametersInfo(request);
        request.setAttribute(SHOW_IMAGES, paramInfo.getShowImages());
        request.setAttribute(TITLE, paramInfo.getTitle());
        request.setAttribute(SHOW_ALL_TEXT, paramInfo.getShowAllText());
        request.setAttribute(ORGANIZER_ID, paramInfo.getOrganizerId());
        if (isBlank(paramInfo.getOrganisationId())) {
            populateEmptyRequest(request, false);
            return;
        }

        EventbriteResults results = getEventbriteResults(paramInfo);
        if (results == null) {
            populateEmptyRequest(request, true);
        } else {
            populateRequest(request, results);
            EventbriteStatusTracker.recordSuccess();
        }
    }

    public EventbriteResults getEventbriteResults(EventsComponentInfo paramInfo) {
        Supplier<EventbriteResults> decoratedSupplier =
                CircuitBreaker.decorateSupplier(circuitBreaker, () -> doGetEventbriteResults(paramInfo));
        try {
            return Try.ofSupplier(decoratedSupplier)
                    .recover(throwable -> new EventbriteResults()).get();
        } catch (ResourceException e) {
            LOG.error("Failed to fetch events", e);
            EventbriteStatusTracker.recordError(e);
            return new EventbriteResults();                // simple Java fallback
        }
    }

    EventbriteResults doGetEventbriteResults(EventsComponentInfo paramInfo) {
        ResourceServiceBroker broker = CrispHstServices.getDefaultResourceServiceBroker(HstServices.getComponentManager());
        Resource results = broker.resolve("eventbrite", URL_TEMPLATE, paramMap(paramInfo));
        ResourceBeanMapper resourceBeanMapper = broker.getResourceBeanMapper("eventbrite");
        return resourceBeanMapper.map(results, EventbriteResults.class);
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