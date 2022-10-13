package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import scot.gov.publishing.hippo.funnelback.component.ResilientSearchComponent;
import scot.gov.publishing.hippo.funnelback.component.SearchComponent;
import scot.gov.publishing.hippo.funnelback.component.SearchSettings;
import scot.mygov.publishing.channels.WebsiteInfo;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.apache.commons.lang3.StringUtils.equalsAny;

/**
 * Set fields required to suppress the header search on some formats.
 *
 * We set the following attributes:
 * - hideSearch - whether to hide the search form
 */
public class SiteHeaderComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        setSearchVisibility(request);
        setSiteTitle(request);
        determineLogo(request);
        populateAutoCompleteFlag(request);
    }

    void setSearchVisibility(HstRequest request) {
        HstComponentConfiguration componentConfig = request
                .getRequestContext()
                .getResolvedSiteMapItem()
                .getHstComponentConfiguration();
        String formatName = componentConfig.getName();
        request.setAttribute("hideSearch", equalsAny(formatName, "home") || formatName.startsWith("search"));
    }

    void setSiteTitle(HstRequest request) {
        Mount mount = request.getRequestContext().getResolvedMount().getMount();
        WebsiteInfo info = mount.getChannelInfo();
        request.setAttribute("siteTitle", info.getSiteTitle());
    }

    static void determineLogo (HstRequest request) {
        Mount mount = request.getRequestContext().getResolvedMount().getMount();
        WebsiteInfo info = mount.getChannelInfo();

        String logoPath = info.getLogoPath();

        if (!logoPath.isEmpty()) {
            try {
                HstRequestContext context = request.getRequestContext();
                Session session = context.getSession();

                Object logo = context.getObjectBeanManager(session).getObject(logoPath);
                request.setAttribute("logo", logo);
            } catch (RepositoryException | ObjectBeanManagerException e) {
                throw new HstComponentException(e);
            }

        }
    }

    /**
     * determine if auto complete should be used for search bars
     */
    static void populateAutoCompleteFlag(HstRequest request) {

        SearchSettings searchSettings = ResilientSearchComponent.searchSettings();
        boolean autoCompleteEnabled = true;
        if (!searchSettings.isEnabled()) {
            autoCompleteEnabled = false;
        }
        if ("bloomreach".equals(searchSettings.getSearchType())) {
            autoCompleteEnabled = false;
        }
        request.setAttribute("autoCompleteEnabled", autoCompleteEnabled);
    }
}
