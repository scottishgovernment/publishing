package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

import java.util.List;

/**
 * Component used to back article pages.
 *
 * Extends the CategoryComponent in order to add prev and next links based on the parent folder.
 */
public class ArticleComponent extends CategoryComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        HippoBean document = request.getRequestContext().getContentBean();

        // CategoryComponent has set an attribute "children" that will list all of the navigable children of the
        // paren page.
        List<HippoBean> children = (List<HippoBean>) request.getAttribute("children");
        int index = children.indexOf(document);
        HippoBean prev = prev(children, index);
        HippoBean next = next(children, index);
        request.setAttribute("prev", prev);
        request.setAttribute("next", next);
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
