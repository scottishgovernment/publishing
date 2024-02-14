package scot.mygov.publishing.valves;

import org.hippoecm.hst.container.valves.AbstractOrderableValve;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.ValveContext;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static scot.gov.variables.VariablesHelper.getVariablesResourceBundle;

public class ResourceBundleContributorValve extends AbstractOrderableValve {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceBundleContributorValve.class);

    private static final String VARIABLES_REQUEST_PARAM = "variables";

    @Override
    public void invoke(final ValveContext valveContext) throws ContainerException {
        try {
            initVariables(valveContext.getRequestContext());
        } catch (RepositoryException e) {
            LOG.error("Failed to get variables resource bundle", e);
        } finally {
            valveContext.invokeNext();
        }
    }

    private void initVariables(HstRequestContext requestContext) throws RepositoryException {
        if (requestContext.getAttribute(VARIABLES_REQUEST_PARAM) == null) {
            Session session = requestContext.getSession();
            requestContext.setAttribute(VARIABLES_REQUEST_PARAM, getVariablesResourceBundle(session));
        }
    }
}
