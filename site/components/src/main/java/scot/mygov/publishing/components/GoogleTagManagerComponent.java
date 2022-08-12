package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;
import scot.mygov.publishing.beans.Base;
import scot.mygov.publishing.beans.Guide;
import scot.mygov.publishing.beans.GuidePage;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Set fields required to correctly output google tag manager fields.
 *
 * We set the following attributes:
 * - gtmName: the format of the page, e.g. article
 * - gtmId: slug for this page
 * - gtmContainerId: the google tag manager container id
 * - gtmAuth: value to use for the gtm_auth parameter
 * - gtmEnv: value to use in the gtm_preview parameter
 * - userType: value to indicate whether the user is internal/external,
 *             for use in the dataLayer
 */
public class GoogleTagManagerComponent extends BaseHstComponent {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleTagManagerComponent.class);

    private HippoUtils hippoUtils = new HippoUtils();

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        setGtmName(request);
        setGtmId(request);
        setUserType(request);
        setMountDependentAttributes(request);


        // set the document to use to populate meta data in the gtm data layer.
        //
        // for guide pages, the guide should be used, and the super ste of reporting tags
        // from the guide and guide page.
        HippoBean document = request.getRequestContext().getContentBean();
        if (document != null) {
            Set<String> reportingTags = new HashSet<>();
            addReportingTags(document, reportingTags);

            // for a guide, also add the reporting tags for the first page
            if (document instanceof Guide) {
                addReportingTags(getFirstGuidePage(request), reportingTags);
            }

            // if this is a guide page, use the guide as the document used to populate the data layer
            if (document instanceof GuidePage) {
                document = getGuide(document);
                addReportingTags(document, reportingTags);
            }

            request.setAttribute("reportingTags", reportingTags);
            request.setAttribute("document", document);
        }
    }

    HippoBean getFirstGuidePage(HstRequest request) {
        HippoFolderBean folder = CategoryComponent.getChildrenFolder(request);
        List<CategoryComponent.Wrapper> children = GuideComponent.getChildren(folder);
        return children.get(0).getBean();
    }

    HippoBean getGuide(HippoBean document) {
        HippoFolderBean folder = (HippoFolderBean) document.getParentBean();
        return folder.getBean("index");

    }

    void addReportingTags(HippoBean bean, Set<String> reportingTags) {
        if (bean instanceof Base) {
            Base base = (Base) bean;
            Collections.addAll(reportingTags, base.getReportingtags());
        }
    }

    /**
     * set gtmName based on the page component from the resolved sitemap item
     *
     * the gtm name is the format of the page, e.g. 'article'
     */
    void setGtmName(HstRequest request) {
        HstComponentConfiguration componentConfig = request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getHstComponentConfiguration();
        String gtmName = componentConfig.getName();
        request.setAttribute("gtmName", gtmName);
    }

    /**
     * set gtmId on the path from the resolved sitemap item
     * the gtmId is the slug of this page
     */
    void setGtmId(HstRequest request) {
        String gtmId = request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getPathInfo();
        request.setAttribute("gtmId", gtmId);
    }

    void setUserType(HstRequest request) {
        String headerUserType = request.getHeader("X-User-Type");
        String userType = defaultString(headerUserType, "internal");
        request.setAttribute("userType", userType);
    }

    /**
     * gtmContainerId, gtmAuth and gtmEnv are set based on the path stored on the mount.
     *
     * This path is used to look up a node containing the relevant values.
     */
    void setMountDependentAttributes(HstRequest request) {

        Mount mount = request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getResolvedMount()
                .getMount();
        String gtmPath = mount.getProperty("publishing:gtm");

        if (isBlank(gtmPath)) {
            LOG.error("Mount has no publishing:gtm: {}", mount.getName());
            setEmptyGtmValues(request);
            return;
        }

        try {
            Node gtmNode = getGtmNode(request, gtmPath);
            if (gtmNode == null) {
                setEmptyGtmValues(request);
                return;
            }
            setGtmValues(request,
                    gtmNode.getProperty("publishing:containerid").getString(),
                    gtmNode.getProperty("publishing:auth").getString(),
                    gtmNode.getProperty("publishing:env").getString());
        } catch (RepositoryException e) {
            LOG.error("Unexpected repository exception trying to set gtm values, gtmPath is {}", gtmPath, e);
            setEmptyGtmValues(request);
        }
    }

    void setGtmValues(HstRequest request, String containerId, String auth, String env) {
        request.setAttribute("gtmAuth", auth);
        request.setAttribute("gtmEnv", env);
        request.setAttribute("gtmContainerId", containerId);
    }

    void setEmptyGtmValues(HstRequest request) {
        setGtmValues(request, "", "", "");
    }

    Node getGtmNode(HstRequest request, String path) throws RepositoryException {
        HstRequestContext requestContext = request.getRequestContext();
        Session session = requestContext.getSession();

        if (!session.nodeExists(path)) {
            LOG.error("gtm path does not exist: {}", path);
            return null;
        }

        Node gtmHandle = session.getNode(path);
        Node gtmNode = hippoUtils.getPublishedVariant(gtmHandle);
        if (gtmNode == null) {
            LOG.info("No published gtm document for path: {}", path);
        }

        return gtmNode;
    }

}