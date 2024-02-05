package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoDocument;

@Node(jcrType="publishing:basedocument")
public class BaseDocument extends HippoDocument {

    public String getUuid() {
        return getSingleProperty("jcr:uuid");
    }

}
