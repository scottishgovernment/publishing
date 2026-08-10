package scot.mygov.publishing.components;

import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.sitemenu.HstSiteMenuItemImpl;
import org.junit.Test;
import org.onehippo.forge.breadcrumb.om.BreadcrumbItem;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MenuComponentTest {

    @Test
    public void expandsItemWhenBreadcrumbLinkMatches() {
        // ARRANGE
        HstSiteMenuItemImpl menuItem = mock(HstSiteMenuItemImpl.class);
        HstLink menuItemLink = link("guidance");
        when(menuItem.getHstLink()).thenReturn(menuItemLink);
        List<BreadcrumbItem> breadcrumbs = singletonList(new BreadcrumbItem(link("guidance"), "Guidance"));

        // ACT
        MenuComponent.setExpanded(menuItem, breadcrumbs);

        // ASSERT
        verify(menuItem).setExpanded();
    }

    @Test
    public void doesNotExpandUnrelatedItemThatHappensToShareAPathSegment() {
        // ARRANGE: menu item "Components" is unrelated to the page being viewed
        // (Guidance -> Tracking -> Components), even though a nested page happens
        // to share its slug.
        HstSiteMenuItemImpl menuItem = mock(HstSiteMenuItemImpl.class);
        HstLink menuItemLink = link("components");
        when(menuItem.getHstLink()).thenReturn(menuItemLink);
        List<BreadcrumbItem> breadcrumbs = singletonList(new BreadcrumbItem(link("guidance"), "Guidance"));

        // ACT
        MenuComponent.setExpanded(menuItem, breadcrumbs);

        // ASSERT
        verify(menuItem, never()).setExpanded();
    }

    @Test
    public void ignoresHomeBreadcrumbEntry() {
        // ARRANGE
        HstSiteMenuItemImpl menuItem = mock(HstSiteMenuItemImpl.class);
        HstLink menuItemLink = link("");
        when(menuItem.getHstLink()).thenReturn(menuItemLink);
        HstLink homeLink = link("");
        List<BreadcrumbItem> breadcrumbs = singletonList(new BreadcrumbItem(homeLink, "Home"));

        // ACT
        MenuComponent.setExpanded(menuItem, breadcrumbs);

        // ASSERT
        verify(menuItem, never()).setExpanded();
    }

    HstLink link(String path) {
        HstLink link = mock(HstLink.class);
        when(link.getPath()).thenReturn(path);
        return link;
    }
}
