package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

import java.util.List;

import static java.lang.Boolean.*;

/**
 * Component used to back article pages.
 *
 * Extends the CategoryComponent in order to add prev and next links based on the parent folder.
 */
public class ArticleComponent extends CategoryComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        setArticleAttributes(request);
    }

    static void setArticleAttributes(HstRequest request) {

        if (!hasContentBean(request)) {
            return;
        }

        // CategoryComponent has set an attribute "children" that will list all of the navigable children of the
        // parent page.
        List<HippoBean> children = (List<HippoBean>) request.getAttribute("children");
        HippoBean document = request.getRequestContext().getContentBean();
        int index = children.indexOf(document);
        HippoBean prev = prev(children, index);
        HippoBean next = next(children, index);
        request.setAttribute("prev", prev);
        request.setAttribute("next", next);
        setSequenceable(request, document);
        request.setAttribute("document", getDocumentBean(request));
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
    static HippoBean prev(List<HippoBean> children, int index) {
        return index > 0
                ? children.get(index - 1)
                : null;
    }

    /**
     * Determine the bean to use for the Next link on this page.  If this is the first child in its parent then
     * this will return null.
     */
    static HippoBean next(List<HippoBean> children, int index) {
        return index < children.size() - 1
                ? children.get(index + 1)
                : null;
    }
}
