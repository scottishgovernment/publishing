package scot.mygov.publishing.components;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.sitemenu.HstSiteMenu;
import org.hippoecm.hst.core.sitemenu.HstSiteMenuItem;
import org.hippoecm.hst.core.sitemenu.HstSiteMenuItemImpl;
import org.onehippo.cms7.essentials.components.EssentialsMenuComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsMenuComponentInfo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersInfo(type = EssentialsMenuComponentInfo.class)
public class MenuComponent extends EssentialsMenuComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        HstSiteMenu menu = request.getModel("menu");
        if (menu == null) {
            return;
        }

        HstLink link = getLink(request);
        Map<String, String> proxymap = getProxyMap(request);
        List<HstSiteMenuItem> menuItems = menu.getSiteMenuItems();
        for (HstSiteMenuItem menuItem : menuItems) {
            setExpanded(menuItem, request, link, proxymap);
        }
    }

    HstLink getLink(HstRequest request) {
        HippoBean contentBean = request.getRequestContext().getContentBean();
        if (contentBean == null) {
            return null;
        }
        HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();
        return linkCreator.create(contentBean, request.getRequestContext());
    }

    void setExpanded(HstSiteMenuItem menuItem, HstRequest request, HstLink link, Map<String, String> proxymap) {
        HstSiteMenuItemImpl itemImpl = (HstSiteMenuItemImpl) menuItem;

        // hande case where a guide is the menu item
        String itemPath = itemImpl.getHstLink().getPath();
        if (link != null && link.getPath().startsWith(itemPath) && StringUtils.isNotBlank(itemPath)) {
            itemImpl.setExpanded();
        }

        String proxyPath = proxymap.get(itemPath);
        HippoBean contentBean = request.getRequestContext().getContentBean();
        if (contentBean == null || proxyPath == null) {
            return;
        }

        if (contentBean.getPath().startsWith(proxyPath)) {
            itemImpl.setExpanded();
        }
    }
    /**
     * For the top level categories of the site, determine the link for any categories with a 'breadcrumbProxy'*
     */
    private Map<String, String> getProxyMap(HstRequest request) {
        HippoBean base = request.getRequestContext().getSiteContentBaseBean();
        HippoFolder browse = base.getBean("browse");
        if (browse == null) {
            return Collections.emptyMap();
        }

        HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();
        List<HippoFolder> folders = browse.getChildBeans(HippoFolder.class);
        Map<String, String> map = new HashMap<>();
        for (HippoFolder folder : folders) {
            HippoBean category = folder.getBean("index");
            if (category != null) {
                HippoBean proxy = category.getLinkedBean("publishing:breadcrumbProxy", HippoBean.class);
                if (proxy != null) {
                    HstLink link = linkCreator.create(proxy, request.getRequestContext());
                    map.put(link.getPath(), category.getParentBean().getPath());
                }
            }
        }
        return map;
    }

}