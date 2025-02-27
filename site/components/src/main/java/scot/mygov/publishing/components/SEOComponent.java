package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.EssentialsDocumentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.beans.ColumnImage;
import scot.mygov.publishing.beans.ImageCard;
import scot.mygov.publishing.channels.WebsiteInfo;

import javax.jcr.RepositoryException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static scot.mygov.publishing.components.SiteHeaderComponent.setCanonical;

/**
 * Provides seo information for the page.
 */
@ParametersInfo(type = SEOComponentInfo.class)
public class SEOComponent extends EssentialsDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SEOComponent.class);

    private static final String SITE_TITLE = "sitetitle";

    private static final String TITLE_TAG = "titletag";

    private static final String PAGE_TITLE = "pagetitle";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        // try to find a seo document first, and then fall back to the content document.
        // this is so that the component can be used in channel manager style pages as in ddat,
        // or baked into the page like mygov
        HippoBean contentBean = (HippoBean) request.getAttribute(REQUEST_ATTR_DOCUMENT);
        if (contentBean == null) {
            contentBean = request.getRequestContext().getContentBean();
        }

        WebsiteInfo websiteInfo = MountUtils.websiteInfo(request);
        setPageTitle(request, websiteInfo.getSiteTitle(), contentBean);
        setPageImage(request, websiteInfo, contentBean);
        request.setAttribute("isSearchEnabled", websiteInfo.isSearchEnabled());
        request.setAttribute("contentBean", contentBean);
        request.setAttribute("baseBean", request.getRequestContext().getSiteContentBaseBean());

        Calendar publicationDate = contentBean.getSingleProperty("publishing:publicationDate");
        if (publicationDate != null) {
            request.setAttribute("date", publicationDate);
        }
        request.setAttribute("subjects", getSubjects(contentBean));
        setCanonical(request);
    }
    void setPageTitle(HstRequest request, String siteTitle, HippoBean contentBean) {

        request.setAttribute(SITE_TITLE, siteTitle);

        if (contentBean == null || contentBean.isHippoFolderBean()) {
            request.setAttribute(TITLE_TAG, siteTitle);
            request.setAttribute(PAGE_TITLE , siteTitle);
            return;
        }

        String pageTitle = contentBean.getSingleProperty("publishing:title");
        if (pageTitle == null) {
            request.setAttribute(TITLE_TAG, siteTitle);
            request.setAttribute(PAGE_TITLE , siteTitle);
        } else {
            request.setAttribute(TITLE_TAG, String.format("%s - %s", pageTitle, siteTitle));
            request.setAttribute(PAGE_TITLE, pageTitle);
        }
    }

    void setPageImage(HstRequest request, WebsiteInfo websiteInfo, HippoBean contentBean) {
        HippoBean imageCard = null;

        if (contentBean != null) {
            imageCard = contentBean.getLinkedBean("publishing:cardImage", ImageCard.class);
        }

        if (imageCard == null) {
            imageCard = NewsComponent.getImage(request);
        }

        if (imageCard == null) {
            imageCard = getDefaultImageCardForSite(request.getRequestContext(), websiteInfo);
        }

        request.setAttribute("cardImage", imageCard);
    }

    static ColumnImage getDefaultImageCardForSite(HstRequestContext requestContext, WebsiteInfo websiteInfo) {
        try {
            Object e = requestContext.getObjectConverter().getObject(
                    requestContext.getSession(), websiteInfo.getDefaultCardImage());
            if (e != null && ColumnImage.class.isAssignableFrom(e.getClass())) {
                return (ColumnImage) e;
            }

            LOG.info("unable to find default card image for request {}", requestContext.getServletRequest().getRequestURI());
            return null;
        } catch (ObjectBeanManagerException | RepositoryException e) {
            LOG.error("Exception trying to get default image card for site {}", websiteInfo.getSiteTitle(), e);
            return null;
        }
    }

    /**
     * The dc.subject is populated from topics and tags
     */
    List<String> getSubjects(HippoBean document) {
        List<String> subjects = new ArrayList<>();
        addValuesIfNotNull(document, "publishing:topics", subjects);
        addValuesIfNotNull(document, "hippostd:tags", subjects);
        return subjects;
    }

    void addValuesIfNotNull(HippoBean document, String prop, List<String> values) {
        String [] propValues = document.getMultipleProperty(prop);
        if (propValues != null) {
            values.addAll(Arrays.asList(propValues));
        }
    }
}
