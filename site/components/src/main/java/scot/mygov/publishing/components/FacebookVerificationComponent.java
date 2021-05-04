package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import scot.mygov.publishing.beans.Facebookverification;

import java.util.List;

public class FacebookVerificationComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response){
        HippoFolder folder = folder(request, "administration/");
        List<Facebookverification> b = folder.getDocuments(Facebookverification.class);
        request.setAttribute("facebookVerifications", b);
    }

    private HippoFolder folder(HstRequest request, String path) {
        HstRequestContext c = request.getRequestContext();
        HippoBean root = c.getSiteContentBaseBean();
        return root.getBean(path, HippoFolder.class);
    }

}
