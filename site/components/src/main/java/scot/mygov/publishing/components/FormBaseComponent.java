package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class FormBaseComponent extends ArticleComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FormBaseComponent.class);

    private static HippoUtils hippoUtils = new HippoUtils();

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        setRecaptchaAttributes(request);
    }

    static void setRecaptchaAttributes(HstRequest request) {

        Mount mount = request.getRequestContext().getResolvedSiteMapItem().getResolvedMount().getMount();
        String path = mount.getProperty("publishing:recaptcha");

        if (isBlank(path)) {
            LOG.error("Mount has no publishing:recaptcha: {}", mount.getName());
            setDefaultValues(request);
            return;
        }

        try {
            Node node = getRecaptchaNode(request, path);
            if (node == null) {
                LOG.error("Unable to find recaptcha node in ");
                setDefaultValues(request);
                return;
            }
            boolean enabled = node.getProperty("publishing:enabled").getBoolean();
            String siteKey = node.getProperty("publishing:sitekey").getString();
            setValues(request, enabled, siteKey);
        } catch (RepositoryException e) {
            LOG.error("Unexpected repository exception trying to set recaptcha values, path is {}", path, e);
            setDefaultValues(request);
        }
    }

    private static void setValues(HstRequest request, boolean enabled, String sitekey) {

        LOG.info("recaptchaEnabled {} {}", enabled, sitekey);
        request.setAttribute("recaptchaEnabled", enabled);
        request.setAttribute("recaptchaSitekey", sitekey);
    }

    private static void setDefaultValues(HstRequest request) {
        setValues(request, false, "");
    }

    private static Node getRecaptchaNode(HstRequest request, String path) throws RepositoryException {
        HstRequestContext requestContext = request.getRequestContext();
        Session session = requestContext.getSession();

        if (!session.nodeExists(path)) {
            LOG.error("recaptcha path does not exist: {}", path);
            return null;
        }

        Node gtmHandle = session.getNode(path);
        Node gtmNode = hippoUtils.getPublishedVariant(gtmHandle);
        if (gtmNode == null) {
            LOG.info("No published recaptcha document for path: {}", path);
        }

        return gtmNode;
    }

}
