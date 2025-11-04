package scot.mygov.publishing.components.eventbrite;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import scot.gov.publishing.hippo.funnelback.client.LoggingHttpRequestInterceptor;
import scot.gov.publishing.hippo.funnelback.client.LoggingHttpResponseInterceptor;

public class EventbriteClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

    /**
     * for eventbrite use a request factory that disables cookie management (prevents warnings being logged), and logs
     * request info
     */
    public EventbriteClientHttpRequestFactory() {
        HttpClient client = HttpClients.custom()
                .disableCookieManagement()
                .addRequestInterceptorFirst(new LoggingHttpRequestInterceptor())
                .addResponseInterceptorFirst(new LoggingHttpResponseInterceptor("eventbrite-http-request"))
                .build();
        setHttpClient(client);
    }
}
