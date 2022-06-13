package scot.mygov.publishing.components.funnelback.postprocess;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import scot.mygov.publishing.components.funnelback.SecurityTokenInterceptor;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class SecurityTokenInterceptorTest {

    @Test
    public void headerSetIfTokenNotBlank() throws IOException {

        // ARRANGE
        SecurityTokenInterceptor sut = new SecurityTokenInterceptor();
        sut.setToken("token");
        HttpRequest request = mock(HttpRequest.class);
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        when(request.getHeaders()).thenReturn(httpHeaders);
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);

        // ACT
        sut.intercept(request, null, execution);

        // ASSERT
        verify(httpHeaders).set("X-Security-Token", "token");
    }

    @Test
    public void noHeaderSetIfTokenBlank() throws IOException {

        // ARRANGE
        SecurityTokenInterceptor sut = new SecurityTokenInterceptor();
        sut.setToken("");
        HttpRequest request = mock(HttpRequest.class);
        HttpHeaders httpHeaders = mock(HttpHeaders.class);
        when(request.getHeaders()).thenReturn(httpHeaders);
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);

        // ACT
        sut.intercept(request, null, execution);

        // ASSERT
        verify(httpHeaders, never()).set(eq("X-Security-Token"), any());
    }
}
