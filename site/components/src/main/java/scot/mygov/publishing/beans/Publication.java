package scot.mygov.publishing.beans;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import org.onehippo.forge.selection.hst.util.SelectionUtil;
import java.util.Calendar;

@HippoEssentialsGenerated(internalName = "publishing:Publication")
@Node(jcrType = "publishing:Publication")
public class Publication extends Base {
    @HippoEssentialsGenerated(internalName = "publishing:publicationDate")
    public Calendar getPublicationDate() {
        return getSingleProperty("publishing:publicationDate");
    }

    @HippoEssentialsGenerated(internalName = "publishing:contentitemlanguage")
    public String getContentitemlanguage() {
        return getSingleProperty("publishing:contentitemlanguage");
    }

    @HippoEssentialsGenerated(internalName = "publishing:topics")
    public String[] getTopics() {
        return getMultipleProperty("publishing:topics");
    }

    @HippoEssentialsGenerated(internalName = "publishing:publicationType")
    public String getPublicationType() {
        return getSingleProperty("publishing:publicationType");
    }

    public String getLabel() {
        HstRequestContext context = RequestContextProvider.get();
        ValueList publicationValueList = SelectionUtil.getValueListByIdentifier("publicationTypes", context);
        return SelectionUtil.valueListAsMap(publicationValueList).getOrDefault(getPublicationType(), "Publication");
    }
}
