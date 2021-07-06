package scot.mygov.publishing.filters;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sets the X-Forwarded-Proto header to https when behind a load balancer.
 * ALBs override the value of the X-Forwarded-Proto header to indicate the
 * protocol used for the request to the ALB. However, with an internal ALB,
 * this protocol may not match the protocol used by the users browser. In
 * our environments, http requests never reach Bloomreach HST, so the correct
 * value for the header is always https.
 *
 * This is a workaround for the following issue:
 * https://issues.onehippo.com/browse/CMS-14725
 */
public class ProtocolFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        boolean behindALB = request.getHeader("X-Amzn-Trace-Id") != null;
        HttpServletRequest req = behindALB ? new FixedProtocolRequest(request) : request;
        super.doFilter(req, response, chain);
    }

    static class FixedProtocolRequest extends HttpServletRequestWrapper {

        public FixedProtocolRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            if ("X-Forwarded-Proto".equalsIgnoreCase(name)) {
                return "https";
            }
            return super.getHeader(name);
        }

    }

}
