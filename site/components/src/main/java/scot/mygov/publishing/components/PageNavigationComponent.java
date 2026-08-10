package scot.mygov.publishing.components;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.components.CommonComponent;

/**
 * Lets a page (in particular an xpage, which has no content document and so
 * cannot use publishing:breadcrumbProxy) nominate the top level navigation item
 * it should be considered part of, by picking the document that top level nav
 * item links to, so that the right item is highlighted regardless of where the
 * page actually lives. See site-navigation.ftl.
 */
@ParametersInfo(type = PageNavigationComponentInfo.class)
public class PageNavigationComponent extends CommonComponent {

    static final String TOP_LEVEL_NAV_ITEM = "topLevelNavItem";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        PageNavigationComponentInfo paramInfo = getComponentParametersInfo(request);
        String linkPath = topLevelNavItemLinkPath(request, paramInfo.getTopLevelNavItem());
        request.setAttribute(TOP_LEVEL_NAV_ITEM, linkPath);
        if (StringUtils.isNotBlank(linkPath)) {
            // set on the request context (rather than the request) so that it is
            // visible to other components regardless of where they sit in the page,
            // see HstRequestContext#setAttribute
            request.getRequestContext().setAttribute(TOP_LEVEL_NAV_ITEM, linkPath);
        }
    }

    private String topLevelNavItemLinkPath(HstRequest request, String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        HippoBean bean = getHippoBeanForPath(path, HippoBean.class);
        if (bean == null) {
            return null;
        }
        HstRequestContext context = request.getRequestContext();
        HstLink link = context.getHstLinkCreator().create(bean, context);
        return link == null ? null : link.getPath();
    }
}
