package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

import java.util.List;

/**
 * Component used to back guides
 *
 * Show the content from the first page.
 */
public class GuideComponent extends CategoryComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        request.setAttribute("guide", getDocumentBean(request));
        HippoBean firstPage = getFirstGuidePage(request);
        ArticleComponent.doSetArticleAttributes(request, firstPage);
    }

    static HippoBean getFirstGuidePage(HstRequest request) {
        List<Wrapper> children = (List<Wrapper>) request.getAttribute("children");
        return children.isEmpty() ? null : children.get(0).getBean();
    }
}
