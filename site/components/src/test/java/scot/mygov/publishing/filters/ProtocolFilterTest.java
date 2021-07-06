package scot.mygov.publishing.filters;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ProtocolFilterTest {

    private ProtocolFilter filter = new ProtocolFilter();

    private FilterChain chain = mock(FilterChain.class);

    private MockHttpServletRequest req = new MockHttpServletRequest();

    private MockHttpServletResponse resp = new MockHttpServletResponse();

    private ArgumentCaptor<HttpServletRequest> captor = ArgumentCaptor.forClass(HttpServletRequest.class);

    @Test
    public void usesForwardedProtocolInLowAvailabilityEnvironment() throws Exception {
        req.addHeader("X-Forwarded-Proto", "http");
        filter.doFilter(req, resp, chain);
        verify(chain).doFilter(captor.capture(), eq(resp));
        HttpServletRequest result = captor.getValue();
        assertThat(result.getHeader("X-Forwarded-Proto")).isEqualTo("http");
    }


    @Test
    public void usesHttpsInHighAvailabilityEnvironment() throws Exception {
        req.addHeader("X-Forwarded-Proto", "http");
        req.addHeader("X-Amzn-Trace-Id", "ARBITRARY_TRACE_ID");
        filter.doFilter(req, resp, chain);
        verify(chain).doFilter(captor.capture(), eq(resp));
        HttpServletRequest result = captor.getValue();
        assertThat(result.getHeader("X-Forwarded-Proto")).isEqualTo("https");
    }

}
