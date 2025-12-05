package scot.mygov.publishing.components.eventbrite;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import scot.gov.publishing.hippo.funnelback.client.LoggingHttpRequestInterceptor;
import scot.gov.publishing.hippo.funnelback.client.LoggingHttpResponseInterceptor;

public class EventbriteClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

    /**
     * for eventbrite use a request factory that disables cookie management (prevents warnings being logged),  logs
     * request info, sets the size of the connection pool and timeout.
     */
    public EventbriteClientHttpRequestFactory() {
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connectionManager())
                .setDefaultRequestConfig(requestConfig())
                .disableCookieManagement()
                .addRequestInterceptorFirst(new LoggingHttpRequestInterceptor())
                .addResponseInterceptorFirst(new LoggingHttpResponseInterceptor("eventbrite-http-request"))
                .build();
        setHttpClient(client);
    }

    PoolingHttpClientConnectionManager connectionManager() {
        // how long HttpClient will wait to establish the TCP/TLS connection before giving up,
        // fail-fast protection against unreachable or sick downstream servers.
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setConnectTimeout(Timeout.ofSeconds(1)).build();
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(connectionConfig)
                .setMaxConnTotal(10)
                .build();
    }

    RequestConfig requestConfig() {
        return RequestConfig.custom()
                // How long your thread will wait for a free connection from the HttpClient connection pool.
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(500))
                // The maximum amount of time to wait for the server to send ANY response data after the request is
                // sent and the connection is established.
                .setResponseTimeout(Timeout.ofMilliseconds(1500))
                .build();
    }
}
