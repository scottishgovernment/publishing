package scot.mygov.publishing.valves;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.container.valves.AbstractOrderableValve;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.ValveContext;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Calendar;

import static org.apache.commons.lang3.StringUtils.endsWith;

public class PreviewValve extends AbstractOrderableValve {

    private static final Logger LOG = LoggerFactory.getLogger(PreviewValve.class);
    private static final String PREVIEW_KEY = "previewkey";

    @Override
    public void invoke(ValveContext context) throws ContainerException {
        HstRequestContext requestContext = context.getRequestContext();
        Mount resolvedMount = requestContext.getResolvedMount().getMount();

        try {
            if (isPreviewMount(resolvedMount)) {
                doInvoke(context, requestContext, resolvedMount);
            }
        } catch (RepositoryException repositoryException) {
            LOG.error("Something with repo went wrong while accessing this node {}.", requestContext.getSiteContentBaseBean(), repositoryException);
        } catch (IOException ioException) {
            LOG.error("Something with IO went wrong while accessing this node {}.", requestContext.getSiteContentBaseBean(), ioException);
        } finally {
            context.invokeNext();
        }
    }

    void doInvoke(ValveContext valveContext, HstRequestContext requestContext, Mount resolvedMount)
            throws RepositoryException, IOException {

        //fetching the content bean
        HippoBean contentBean = requestContext.getContentBean();

        //fetching the previewkey
        String previewKey = getPreviewKey(valveContext, resolvedMount);
        LOG.info("previewKey is {}", previewKey);

        //intercepting requests having the id in the url
        if (contentBean == null) {
            LOG.info("Preview request doesn't contain content bean");
            requestContext.getServletResponse().sendError(403);
            return;
        }

        if (StringUtils.isBlank(previewKey)) {
            LOG.info("Preview request doesn't contain preview key");
            requestContext.getServletResponse().sendError(403);
            return;
        }

        if (!hasValidStagingKey(contentBean, previewKey)) {
            LOG.info("Preview key {} for document {} is invalid or preview link has expired.", previewKey, contentBean.getPath());
            requestContext.getServletResponse().sendError(403);
        }
    }

    boolean isPreviewMount(Mount resolvedMount) {
        if (resolvedMount == null) {
            LOG.debug("resolved mount is null");
            return false;
        }

        return "preview".equals(resolvedMount.getType()) && endsWith(resolvedMount.getAlias(), "-staging");
    }

    boolean hasValidStagingKey(HippoBean contentBean, String previewKey) throws RepositoryException {
        Node unpublishedNode = getUnpublishedNode(contentBean);
        NodeIterator iterator = unpublishedNode.getNodes("previewId");
        LOG.info("hasValidStagingKey {}, {} previewkeys", contentBean.getPath(), iterator.getSize());
        while (iterator.hasNext()) {
            Node node = iterator.nextNode();
            Calendar expirationCalendar = JcrUtils.getDateProperty(node, "staging:expirationdate", null);
            if (JcrUtils.getStringProperty(node, "staging:key", "").equals(previewKey) && isValid(expirationCalendar)) {
                return true;
            }
        }
        return false;
    }

    Node getUnpublishedNode(HippoBean contentBean) throws RepositoryException {
        Node handle = contentBean.getNode().getParent();
        for (Node variant : new NodeIterable(handle.getNodes(handle.getName()))) {
            final String state = JcrUtils.getStringProperty(variant, HippoStdNodeType.HIPPOSTD_STATE, null);
            if (HippoStdNodeType.UNPUBLISHED.equals(state)) {
                return variant;
            }
        }
        return null;
    }

    private boolean isValid(final Calendar expirationCalendar){
        return expirationCalendar == null || Calendar.getInstance().before(expirationCalendar);
    }

    private String getPreviewKey(final ValveContext context, final Mount resolvedMount){
        String previewKey = context.getServletRequest().getParameter(PREVIEW_KEY);
        Cookie previewCookie = getPreviewCookie(context);

        if(StringUtils.isNotEmpty(previewKey) && previewCookie == null){
            Cookie cookie = new Cookie(PREVIEW_KEY, previewKey);
            cookie.setPath(getPath(context.getServletRequest()));
            boolean httpOnly = Boolean.parseBoolean(resolvedMount.getProperty("preview-cookie-httponly"));
            boolean secure = Boolean.parseBoolean(resolvedMount.getProperty("preview-cookie-secure"));
            cookie.setHttpOnly(httpOnly);
            cookie.setSecure(secure);
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

    private static String getPath(HttpServletRequest request) {
        final String contextPath = request.getContextPath();
        if (StringUtils.isBlank(contextPath)) {
            return "/";
        }
        return contextPath;
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

