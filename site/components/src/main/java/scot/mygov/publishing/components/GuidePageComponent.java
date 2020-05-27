package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

public class GuidePageComponent extends ArticleComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HippoBean page = getDocumentBean(request);
        HippoFolderBean folder = (HippoFolderBean) page.getParentBean();
        HippoBean guide = folder.getBean("index");
        request.setAttribute("guide", guide);
    }

}
