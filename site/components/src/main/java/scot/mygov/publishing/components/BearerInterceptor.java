package scot.mygov.publishing.components;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class BearerInterceptor implements ClientHttpRequestInterceptor {

    // token is configured in hst.properties or can be passed in the command line when running in cargo
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
