package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.configuration.hosting.VirtualHosts;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.platform.model.HstModel;
import org.onehippo.cms7.services.cmscontext.CmsSessionContext;
import scot.mygov.publishing.channels.WebsiteInfo;

import jakarta.servlet.http.HttpSession;

import static org.hippoecm.hst.core.container.ContainerConstants.CMS_REQUEST_RENDERING_MOUNT_ID;

public class MountUtils {

    public static final String ALIAS = "publishing:alias";

    private MountUtils() {
        // prevent instantiation
    }

    public static Mount getEditingMount(HstRequestContext hstRequestContext) {
        String mountId = getRenderingMountId(hstRequestContext);
        Mount mount = getEditingPreviewVirtualHosts(hstRequestContext).getMountByIdentifier(mountId);
        if (mount == null) {
            String msg = String.format("Could not find a Mount for identifier + '%s'", mountId);
            throw new IllegalStateException(msg);
        }
        if (!Mount.PREVIEW_NAME.equals(mount.getType())) {
            String msg = String.format("Expected a preview (decorated) mount but '%s' is not of " +
                    "type preview.", mount.toString());
            throw new IllegalStateException(msg);
        }
        return mount;
    }

    private static String getRenderingMountId( HstRequestContext hstRequestContext ) {
        HttpSession httpSession = hstRequestContext.getServletRequest().getSession();
        CmsSessionContext cmsSessionContext = CmsSessionContext.getContext(httpSession);
        String renderingMountId = (String) cmsSessionContext.getContextPayload().get(CMS_REQUEST_RENDERING_MOUNT_ID);
        if (renderingMountId == null) {
            throw new IllegalStateException("Could not find rendering mount id on request session.");
        }

        return renderingMountId;
    }

    private static VirtualHosts getEditingPreviewVirtualHosts(HstRequestContext hstRequestContext) {
        return getPreviewHstModel( hstRequestContext).getVirtualHosts();
    }

    private static HstModel getPreviewHstModel(HstRequestContext hstRequestContext ) {
        return (HstModel) hstRequestContext.getAttribute("org.hippoecm.hst.pagecomposer.jaxrs.services.PageComposerContextService.preview");
    }

    /**
     * Returns the WebsiteInfo for the resolved mount for the request.
     * If none is found, but the mount has a publishing:alias property,
     * then return the WebsiteInfo for a mount with an hst:alias of the
     * same value.
     * This allows use of the WebsiteInfo for additional mounts for a
     * same site, without Bloomreach warning about duplicate channels.
     * See also:
     * https://xmdocumentation.bloomreach.com/library/concepts/hst-configuration-model/advanced/explicitly-hide-a-mount-in-the-channel-manager.html
     */
    public static WebsiteInfo websiteInfo(HstRequest request) {
        Mount mount = request.getRequestContext()
                .getResolvedMount()
                .getMount();
        WebsiteInfo info = mount.getChannelInfo();

        if (info == null) {
            mount = request.getRequestContext().getMount(mount.getProperty(ALIAS));
            info = mount.getChannelInfo();
        }
        return info;
    }

}
