package scot.mygov.publishing.beans;

import org.onehippo.cms7.essentials.dashboard.annotations.HippoEssentialsGenerated;
import org.hippoecm.hst.content.beans.Node;

@HippoEssentialsGenerated(internalName = "publishing:contentcontact")
@Node(jcrType = "publishing:contentcontact")
public class Contentcontact extends BaseDocument {
    @HippoEssentialsGenerated(internalName = "publishing:name")
    public String getName() {
        return getSingleProperty("publishing:name");
    }

    @HippoEssentialsGenerated(internalName = "publishing:email")
    public String getEmail() {
        return getSingleProperty("publishing:email");
    }

    @HippoEssentialsGenerated(internalName = "publishing:notes")
    public String getNotes() {
        return getSingleProperty("publishing:notes");
    }

    @HippoEssentialsGenerated(internalName = "publishing:phone")
    public String getPhone() {
        return getSingleProperty("publishing:phone");
    }
}
