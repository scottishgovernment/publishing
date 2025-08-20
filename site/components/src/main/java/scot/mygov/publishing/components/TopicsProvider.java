package scot.mygov.publishing.components;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.util.SelectionUtil;
import scot.gov.publishing.hippo.funnelback.component.MapProvider;

import java.util.Map;

import static java.util.Collections.emptyMap;

public class TopicsProvider implements MapProvider {
    @Override
    public Map<String, String> get(HstRequestContext context) {
        HippoBean baseBean = context.getSiteContentBaseBean();
        HippoBean topicsList = getTopicsList(baseBean);
        if (topicsList == null) {
            return emptyMap();
        }
        return SelectionUtil.valueListAsMap((ValueList) topicsList);
    }

    static HippoBean getTopicsList(HippoBean baseBean) {
        HippoFolder administration = baseBean.getBean("administration");
        if (administration == null) {
            return null;
        }

        return administration.getBean("topics");
    }
}
