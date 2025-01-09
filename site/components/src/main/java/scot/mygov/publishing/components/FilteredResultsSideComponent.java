package scot.mygov.publishing.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

import static scot.mygov.publishing.components.FilteredResultsComponent.*;

public class FilteredResultsSideComponent extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        String query = param(request, "q");
        Map<String, String> topics = topics(request);
        LocalDate begin = date(request, "begin");
        LocalDate end = date(request, "end");

        request.setAttribute("term", query);
        request.setAttribute("topics", topics);
        request.setAttribute("begin", begin);
        request.setAttribute("end", end);
    }
}
