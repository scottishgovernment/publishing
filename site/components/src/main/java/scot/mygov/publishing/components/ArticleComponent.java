package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;

import java.util.List;

import static java.lang.Boolean.*;

/**
 * Component used to back article pages.
 *
 * Extends the CategoryComponent in order to add prev and next links based on the parent folder.
 */
public class ArticleComponent extends CategoryComponent {

    static Redirector redirector = new Redirector();

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        setArticleAttributes(request, response);
    }

    static void setArticleAttributes(HstRequest request, HstResponse response) {
        if (!hasContentBean(request)) {
            return;
        }

        // if this is not the canonical url then redirect to that url
        String canonicalUrl = canonicalUrl(request);
        if (!canonicalUrl.equals(request.getPathInfo())) {
            redirector.sendPermanentRedirect(request, response, canonicalUrl);
            return;
        }

        // CategoryComponent has set an attribute "children" that will list all of the navigable children of the
        // parent page.
        doSetArticleAttributes(request);
    }

    static void doSetArticleAttributes(HstRequest request) {
        HippoBean document = getDocumentBean(request);
        doSetArticleAttributes(request, document);
    }

    static void doSetArticleAttributes(HstRequest request, HippoBean document) {
        List<Wrapper> children = (List<Wrapper>) request.getAttribute("children");
        int index = indexOf(children, document);
        HippoBean prev = prev(children, index);
        HippoBean next = next(children, index);
        request.setAttribute("prev", prev);
        request.setAttribute("next", next);
        setSequenceable(request, document);
        request.setAttribute("document", document);
    }

    static int indexOf(List<Wrapper> list, HippoBean bean) {
        int index = 0;
        for (Wrapper wrapper : list) {
            if (bean.isSelf(wrapper.getBean())) {
                return index;
            }
            index++;
        }
        return index;
    }

    static String canonicalUrl(HstRequest request ) {
        HippoBean document = request.getRequestContext().getContentBean();
        return canonicalUrl(request, document);
    }

    static String canonicalUrl(HstRequest request, HippoBean document) {
        HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();
        HstLink redirectTolink = linkCreator.create(document, request.getRequestContext());
        return "/" + redirectTolink.getPath();
    }

    /**
     * Set "sequenceable" flag on the request that is determined by the property of the parent category page.
     */
    static void setSequenceable(HstRequest request, HippoBean document) {
        HippoBean index = indexBean(document.getParentBean());
        Boolean sequenceable =
                index != null
                ? index.getSingleProperty("publishing:sequenceable")
                : FALSE;
        request.setAttribute("sequenceable", sequenceable);
    }

    /**
     * Determine the bean to use for the Previous link on this page.  If this is the last child in its parent then
     * this will return null.
     */
    static HippoBean prev(List<Wrapper> children, int index) {
        return index > 0
                ? children.get(index - 1).getBean()
                : null;
    }

    /**
     * Determine the bean to use for the Next link on this page.  If this is the first child in its parent then
     * this will return null.
     */
    static HippoBean next(List<Wrapper> children, int index) {
        return index < children.size() - 1
                ? children.get(index + 1).getBean()
                : null;
    }

}
