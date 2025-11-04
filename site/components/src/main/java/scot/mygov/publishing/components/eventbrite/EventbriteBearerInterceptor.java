package scot.mygov.publishing.components.eventbrite;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class EventbriteBearerInterceptor implements ClientHttpRequestInterceptor {

    // token is set in custom-resource-resolvers.xml, it sets it to the value of the property eventbrite.token
    // which can be configured in hst.properties or can be passed in the command line when running in cargo
    String token;

    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
        request.getHeaders().set("Authorization", "Bearer " + getToken());
        return execution.execute(request, bytes);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
