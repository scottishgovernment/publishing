package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.manager.ObjectBeanManager;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedMount;
import org.junit.Test;
import scot.mygov.publishing.channels.WebsiteInfo;

import javax.jcr.Session;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SiteHeaderComponentTest {

    @Test
    public void getsChannelLogo() throws Exception {
        // ARRANGE
        Mount mount = mock(Mount.class);
        ResolvedMount resolvedMount = mock(ResolvedMount.class);
        WebsiteInfo info = mock(WebsiteInfo.class);
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        Session session = mock(Session.class);
        ObjectBeanManager manager = mock(ObjectBeanManager.class);
        Object object = mock(Object.class);

        when(request.getRequestContext()).thenReturn(context);
        when(context.getResolvedMount()).thenReturn(resolvedMount);
        when(resolvedMount.getMount()).thenReturn(mount);
        when(mount.getChannelInfo()).thenReturn(info);
        when(info.getLogo()).thenReturn("logo");
        when(context.getSession()).thenReturn(session);
        when(context.getObjectBeanManager(session)).thenReturn(manager);
        when(manager.getObject(anyString())).thenReturn(object);

        // ACT
        SiteHeaderComponent.setWebsiteInfo(request);

    }

    @Test
    public void noChannelLogo() throws Exception {
        // ARRANGE
        Mount mount = mock(Mount.class);
        ResolvedMount resolvedMount = mock(ResolvedMount.class);
        WebsiteInfo info = mock(WebsiteInfo.class);
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        Session session = mock(Session.class);
        ObjectBeanManager manager = mock(ObjectBeanManager.class);
        Object object = mock(Object.class);

        when(request.getRequestContext()).thenReturn(context);
        when(context.getResolvedMount()).thenReturn(resolvedMount);
        when(resolvedMount.getMount()).thenReturn(mount);
        when(mount.getChannelInfo()).thenReturn(info);
        when(info.getLogo()).thenReturn("");
        when(context.getSession()).thenReturn(session);
        when(context.getObjectBeanManager(session)).thenReturn(manager);
        when(manager.getObject(anyString())).thenReturn(object);

        // ACT
        SiteHeaderComponent.setWebsiteInfo(request);

    }

    @Test
    public void getSiteTitle() {
        SiteHeaderComponent sut = new SiteHeaderComponent();
        Mount mount = mock(Mount.class);
        ResolvedMount resolvedMount = mock(ResolvedMount.class);
        WebsiteInfo info = mock(WebsiteInfo.class);
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);

        when(request.getRequestContext()).thenReturn(context);
        when(context.getResolvedMount()).thenReturn(resolvedMount);
        when(resolvedMount.getMount()).thenReturn(mount);
        when(mount.getChannelInfo()).thenReturn(info);
        when(info.getSiteTitle()).thenReturn("Site title");

        sut.setWebsiteInfo(request);
    }

}
