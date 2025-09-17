package scot.mygov.publishing.components;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.sitemenu.HstSiteMenu;
import org.hippoecm.hst.core.sitemenu.HstSiteMenuItem;
import org.hippoecm.hst.core.sitemenu.HstSiteMenuItemImpl;
import org.onehippo.cms7.essentials.components.EssentialsMenuComponent;
import org.onehippo.cms7.essentials.components.info.EssentialsMenuComponentInfo;
import org.onehippo.forge.breadcrumb.om.BreadcrumbItem;

import java.util.List;

import static scot.mygov.publishing.components.BreadcrumbComponent.constructBreadcrumb;

@ParametersInfo(type = EssentialsMenuComponentInfo.class)
public class MenuComponent extends EssentialsMenuComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);

        HstSiteMenu menu = request.getModel("menu");
        if (menu == null) {
            return;
        }

        List<BreadcrumbItem> breadcrumbs = constructBreadcrumb(request, request.getRequestContext().getContentBean());
        List<HstSiteMenuItem> menuItems = menu.getSiteMenuItems();
        for (HstSiteMenuItem menuItem : menuItems) {
            setExpanded(menuItem, breadcrumbs);
        }
    }

    void setExpanded(HstSiteMenuItem menuItem, List<BreadcrumbItem> breadcrumbs) {
        HstSiteMenuItemImpl itemImpl = (HstSiteMenuItemImpl) menuItem;
        for (BreadcrumbItem breadcrumb : breadcrumbs) {
            if (sameLink(breadcrumb.getLink(), itemImpl.getHstLink())) {
                itemImpl.setExpanded();
            }
        }
    }

    boolean sameLink(HstLink left, HstLink right) {
        if (left == null || right == null) {
            return false;
        }
        return StringUtils.equals(linkPath(left), linkPath(right));
    }

    String linkPath(HstLink link) {
        return link == null ? null : link.getPath();
    }
}