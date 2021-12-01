package scot.mygov.publishing.valves;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.request.HstRequestContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.apache.commons.lang3.StringUtils.endsWith;


public class PreviewKeyUtils {

    private static final String PREVIEW_KEY = "previewkey";

    private PreviewKeyUtils() {
        // prevent instantiation
    }

    public static String getPreviewKey(
            HttpServletRequest servletRequest,
            HttpServletResponse servletResponse,
            Mount resolvedMount){
        String previewKey = servletRequest.getParameter(PREVIEW_KEY);
        Cookie previewCookie = getPreviewCookie(servletRequest);

        if(StringUtils.isNotEmpty(previewKey) && previewCookie == null){
            Cookie cookie = new Cookie(PREVIEW_KEY, previewKey);
            cookie.setPath(getPath(servletRequest));
            boolean httpOnly = Boolean.parseBoolean(resolvedMount.getProperty("preview-cookie-httponly"));
            boolean secure = Boolean.parseBoolean(resolvedMount.getProperty("preview-cookie-secure"));
            cookie.setHttpOnly(httpOnly);
            cookie.setSecure(secure);
            servletResponse.addCookie(cookie);
            return previewKey;
        } else if (StringUtils.isNotEmpty(previewKey) && previewCookie != null){
            if(!previewKey.equals(previewCookie.getValue())){
                previewCookie.setValue(previewKey);
                servletResponse.addCookie(previewCookie);
            }
            return previewKey;
        } else if (StringUtils.isEmpty(previewKey) && previewCookie != null){
            return previewCookie.getValue();
        } else {
            return null;
        }
    }

    public static boolean isPreviewMount(HstRequestContext requestContext) {
        Mount resolvedMount = requestContext.getResolvedMount().getMount();
        if (resolvedMount == null) {
            return false;
        }
        return "preview".equals(resolvedMount.getType())
                && endsWith(resolvedMount.getAlias(), "-staging");
    }

    private static String getPath(HttpServletRequest request) {
        final String contextPath = request.getContextPath();
        if (StringUtils.isBlank(contextPath)) {
            return "/";
        }
        return contextPath;
    }

    private static Cookie getPreviewCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null) {
            for (Cookie cookie : cookies) {
                if (PREVIEW_KEY.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
