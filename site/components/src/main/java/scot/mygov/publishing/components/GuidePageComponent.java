package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

import static scot.mygov.publishing.components.GuideComponent.getFirstGuidePage;

public class GuidePageComponent extends ArticleComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HippoBean page = getDocumentBean(request);
        HippoFolderBean folder = (HippoFolderBean) page.getParentBean();
        HippoBean guide = folder.getBean("index");
        HippoBean firstPage = getFirstGuidePage(request);
        request.setAttribute("guide", guide);
        request.setAttribute("firstPage", page.isSelf(firstPage));
    }

}
