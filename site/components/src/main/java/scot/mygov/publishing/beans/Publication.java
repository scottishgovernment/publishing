package scot.mygov.publishing.beans;

import org.hippoecm.hst.container.RequestContextProvider;
import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import scot.mygov.publishing.components.PublicationTypesProvider;

import java.util.Calendar;
import java.util.Map;

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
        Map<String, String> publicationTypes = new PublicationTypesProvider(false).get(RequestContextProvider.get());
        String publicationType = getPublicationType();
        return publicationTypes.containsKey(getPublicationType()) ? publicationTypes.get(publicationType) : "Publication";
    }
}