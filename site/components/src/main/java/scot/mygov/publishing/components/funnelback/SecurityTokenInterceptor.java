package scot.mygov.publishing.components.funnelback;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class SecurityTokenInterceptor implements ClientHttpRequestInterceptor {

    private String token;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {

        if (isNotBlank(token)) {
            request.getHeaders().set("X-Security-Token", token);
        }
        return execution.execute(request, bytes);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
