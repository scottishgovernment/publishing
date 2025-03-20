package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import scot.mygov.publishing.beans.Smartanswerquestion;
import scot.mygov.publishing.beans.Smartanswerresult;

import java.util.List;

/**
 * All the files for a smart answer are contained within a folder.
 *
 * The document will contain standard fields such as title, summary etc.  The questions ans answers are then all
 * listed in order.
 */
public class SmartAnswerComponent extends EssentialsContentComponent {


    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        HippoBean document = request.getRequestContext().getContentBean();
        HippoFolder folder = (HippoFolder) document.getParentBean();
        List<Smartanswerquestion> questions = folder.getDocuments(Smartanswerquestion.class);
        List<Smartanswerresult> answers = folder.getDocuments(Smartanswerresult.class);
        request.setAttribute("document", document);
        request.setAttribute("questions", questions);
        request.setAttribute("answers", answers);
        request.setAttribute("root", request.getRequestContext().getSiteContentBaseBean());
    }

}
