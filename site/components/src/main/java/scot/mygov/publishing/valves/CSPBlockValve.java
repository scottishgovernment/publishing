package scot.mygov.publishing.valves;

import jakarta.servlet.http.HttpServletRequest;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.container.valves.AbstractOrderableValve;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.ValveContext;
import org.hippoecm.hst.core.request.HstRequestContext;

/**
 * Disables the Content-Security-Policy header for forms.
 * These forms do not currently work with the CSP header present.
 */
public class CSPBlockValve extends AbstractOrderableValve {

    public void invoke(ValveContext valveContext) throws ContainerException {
        HstRequestContext requestContext = valveContext.getRequestContext();
        Mount mount = requestContext.getResolvedMount().getMount();
        if (isMygov(mount) && isForm(requestContext)) {
            HttpServletRequest request = valveContext.getServletRequest();
            request.setAttribute("NO_CSP", true);
        }
        valveContext.invokeNext();
    }

    private boolean isMygov(Mount mount) {
        return mount.getAlias().startsWith("mygov");
    }

    private boolean isForm(HstRequestContext requestContext) {
        return switch (requestContext.getServletRequest().getPathInfo()) {
            case "/create-notice-to-leave",
                 "/create-rent-increase-notice",
                 "/create-tenancy-agreement",
                 "/non-domestic-rates-calculator" -> true;
            default -> false;
        };
    }

}
