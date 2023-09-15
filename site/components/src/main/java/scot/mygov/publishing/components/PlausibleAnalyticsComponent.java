package scot.mygov.publishing.components;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.beans.FormBase;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.Arrays;
import java.util.stream.Collectors;

import static scot.mygov.publishing.components.GoogleTagManagerComponent.getGtmNode;

public class PlausibleAnalyticsComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PlausibleAnalyticsComponent.class);

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        try {
            String domains = getDomainsString(request);
            setPlausibleDomains(request, domains);
            setRequiresHashScript(request);
        } catch (RepositoryException e) {
            LOG.error("Unexpected repository exception trying to set gtm values", e);
            setPlausibleDomains(request, "");
        }
    }

    void setRequiresHashScript(HstRequest request) {
        HippoBean contentBean = request.getRequestContext().getContentBean();
        boolean requiresHashScript = contentBean != null && contentBean instanceof FormBase;
        request.setAttribute("requiresPlausibleAnalyticsHashScript", requiresHashScript);
    }

    String getDomainsString(HstRequest request) throws RepositoryException {
        Node gtmNode = getGtmNode(request);
        if (gtmNode == null) {
            return "";
        }
        return getPlausibleDomains(gtmNode);
    }

    String getPlausibleDomains(Node gtmNode) throws RepositoryException {
        if (!gtmNode.hasProperty("publishing:plausibleAnalyticsDomains")) {
            return "";
        }

        Value[] values = gtmNode.getProperty("publishing:plausibleAnalyticsDomains").getValues();
        return Arrays.stream(values)
                .map(this::getValue)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(","));
    }

    void setPlausibleDomains(HstRequest request, String domains) {
        request.setAttribute("plausibleAnalyticsDomains", domains);
    }

    String getValue(Value value) {
        try {
            return value.getString();
        } catch (RepositoryException e) {
            LOG.warn("Unable to get plausible domain domain value", e);
            return "";
        }
    }
}
