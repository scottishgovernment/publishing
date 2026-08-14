package scot.mygov.publishing.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoDocument;

import java.util.Calendar;

@Node(jcrType="publishing:basedocument")
public class BaseDocument extends HippoDocument {

    public String getUuid() {
        return getSingleProperty("jcr:uuid");
    }

    public Calendar getLastUpdatedDate() {
        return getSingleProperty("publishing:publicationDate");
    }

}
