package scot.mygov.publishing.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.parameters.ValueListProvider;
import static org.hippoecm.hst.core.container.ContainerConstants.CMS_REQUEST_RENDERING_MOUNT_ID;

import javax.servlet.http.HttpSession;

import org.hippoecm.hst.configuration.hosting.VirtualHosts;
import org.hippoecm.hst.platform.model.HstModel;
import org.onehippo.cms7.services.cmscontext.CmsSessionContext;
import scot.mygov.publishing.channels.WebsiteInfo;

public class ComponentColourValueListProvider implements ValueListProvider {
    private static Map<String, String> colourValuesMap = new LinkedHashMap<>();
    private static List<String> colourValuesList;

    public Mount getEditingMount(HstRequestContext hstRequestContext) {
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

    private String getRenderingMountId( HstRequestContext hstRequestContext ) {
        HttpSession httpSession = hstRequestContext.getServletRequest().getSession();
        CmsSessionContext cmsSessionContext = CmsSessionContext.getContext(httpSession);
        String renderingMountId = (String) cmsSessionContext.getContextPayload().get(CMS_REQUEST_RENDERING_MOUNT_ID);
        if (renderingMountId == null) {
            throw new IllegalStateException("Could not find rendering mount id on request session.");
        }

        return renderingMountId;
    }

    private VirtualHosts getEditingPreviewVirtualHosts( HstRequestContext hstRequestContext) {
      return getPreviewHstModel( hstRequestContext).getVirtualHosts();
    }

    private HstModel getPreviewHstModel( HstRequestContext hstRequestContext ) {
        return (HstModel) hstRequestContext.getAttribute("org.hippoecm.hst.pagecomposer.jaxrs.services.PageComposerContextService.preview");
    }

    private List getValueListForLocale(Locale locale) {
        HstRequestContext hstRequestContext = RequestContextProvider.get();
        Mount editingMount = getEditingMount(hstRequestContext);
        WebsiteInfo siteInfo = editingMount.getChannelInfo();

        colourValuesMap.clear();

        colourValuesMap.put("white", "White");
        colourValuesMap.put("blue", "Blue");
        colourValuesMap.put("darkblue", "Dark blue");
        colourValuesMap.put("grey", "Light grey");

        String color1 = siteInfo.getColor1();
        String color2 = siteInfo.getColor2();

        if (color1.isEmpty()) {} else {
            colourValuesMap.put(color1, color1);
        }

        if (color2.isEmpty()) {} else {
            colourValuesMap.put(color2, color2);
        }

        return colourValuesList = Collections.unmodifiableList(new LinkedList<>(colourValuesMap.keySet()));
    }

    @Override
    public List<String> getValues() {
        List list = getValueListForLocale(null);
        if (list != null) {
            return list;
        }

        // if no color list found, return empty list.
        return new ArrayList<String>();
    }

    @Override
    public String getDisplayValue(String value) {
        return getDisplayValue(value, null);
    }

    @Override
    public String getDisplayValue(String value, Locale locale) {
        String displayValue = colourValuesMap.get(value);
        return (displayValue != null) ? displayValue : value;
    }
}
