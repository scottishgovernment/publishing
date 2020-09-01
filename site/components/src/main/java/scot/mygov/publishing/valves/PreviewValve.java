package scot.mygov.publishing.valves;

import java.io.IOException;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.container.valves.AbstractOrderableValve;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.ValveContext;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.repository.util.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreviewValve extends AbstractOrderableValve {

    private static final Logger log = LoggerFactory.getLogger(PreviewValve.class);
    private static final String PREVIEW_KEY = "previewkey";

    @Override
    public void invoke(ValveContext context) throws ContainerException {
        try {
            HstRequestContext requestContext = context.getRequestContext();
            Mount resolvedMount = requestContext.getResolvedMount().getMount();
            if ("preview".equals(resolvedMount.getType()) && resolvedMount.getAlias().endsWith("-staging")) {
                //fetching the content bean
                HippoBean contentBean = requestContext.getContentBean();
                //fetching the previewkey
                String previewKey = getPreviewKey(context);
                try {
                    //intercepting requests having the id in the url
                    if (contentBean != null && StringUtils.isNotBlank(previewKey)) {
                        boolean found = false;
                        NodeIterator iterator = contentBean.getNode().getNodes("previewId");
                        while (iterator.hasNext() && !found) {
                            Node node = iterator.nextNode();
                            Calendar expirationCalendar = JcrUtils.getDateProperty(node, "staging:expirationdate", null);
                            if (JcrUtils.getStringProperty(node, "staging:key", "").equals(previewKey) && isValid(expirationCalendar)) {
                                found = true;
                            }
                        }
                        if (!found) {
                            log.debug("Preview key {} for document {} is invalid or preview link has expired.", contentBean.getPath(), previewKey);
                            requestContext.getServletResponse().sendError(403);
                        }
                    } else {
                        log.debug("Preview request doesn't contain content bean or preview key");
                        requestContext.getServletResponse().sendError(403);
                    }
                } catch (RepositoryException repositoryException) {
                    log.error("Something with repo went wrong while accessing this node {}.", requestContext.getSiteContentBaseBean(), repositoryException);
                } catch (IOException ioException) {
                    log.error("Something with IO went wrong while accessing this node {}.", requestContext.getSiteContentBaseBean(), ioException);
                }
            }
        } finally {
            context.invokeNext();
        }
    }

    private boolean isValid(final Calendar expirationCalendar){
        return expirationCalendar == null || Calendar.getInstance().before(expirationCalendar);
    }

    private String getPreviewKey(final ValveContext context){
        String previewKey = context.getServletRequest().getParameter(PREVIEW_KEY);
        Cookie previewCookie = getPreviewCookie(context);

        if(StringUtils.isNotEmpty(previewKey) && previewCookie == null){
            Cookie cookie = new Cookie(PREVIEW_KEY, previewKey);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            context.getServletResponse().addCookie(cookie);
            return previewKey;
        } else if (StringUtils.isNotEmpty(previewKey) && previewCookie != null){
            if(!previewKey.equals(previewCookie.getValue())){
                previewCookie.setValue(previewKey);
                context.getServletResponse().addCookie(previewCookie);
            }
            return previewKey;
        } else if (StringUtils.isEmpty(previewKey) && previewCookie != null){
            return previewCookie.getValue();
        } else {
            return null;
        }
    }

    private Cookie getPreviewCookie(final ValveContext context){
        Cookie[] cookies = context.getServletRequest().getCookies();
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

