package scot.mygov.publishing;

import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.repository.util.JcrUtils;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import static org.hippoecm.repository.HippoStdNodeType.*;

public class HippoUtils {

    public Node getPublishedVariant(Node handle) throws RepositoryException {
        return getVariantWithState(handle, PUBLISHED);
    }

    public Node getVariantWithState(Node handle, String state) throws RepositoryException {
        String name = handle.getName();
        NodeIterator iterator = handle.getNodes(name);

        while (iterator.hasNext()) {
            Node variant = iterator.nextNode();
            String variantState = JcrUtils.getStringProperty(variant, HIPPOSTD_STATE, null);
            if (variantState.equals(state)) {
                return variant;
            }
        }
        return null;
    }

    /**
     * Have factored this into a non static method for the purposes of mocking in unit tests.
     */
    public HstQueryBuilder createQuery(HippoBean scope) {
        return HstQueryBuilder.create(scope);
    }

}