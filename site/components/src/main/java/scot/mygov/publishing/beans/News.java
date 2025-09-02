package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;
import java.util.Calendar;

@HippoEssentialsGenerated(internalName = "publishing:News")
@Node(jcrType = "publishing:News")
public class News extends Base {
    @HippoEssentialsGenerated(internalName = "publishing:publicationDate")
    public Calendar getPublicationDate() {
        return getSingleProperty("publishing:publicationDate");
    }

    @HippoEssentialsGenerated(internalName = "publishing:showFeedback")
    public Boolean getShowFeedback() {
        return getSingleProperty("publishing:showFeedback");
    }

    @HippoEssentialsGenerated(internalName = "publishing:Image")
    public Image getImage() {
        return getBean("publishing:Image", Image.class);
    }

    public String getLabel() {
        return "news";
    }
}
