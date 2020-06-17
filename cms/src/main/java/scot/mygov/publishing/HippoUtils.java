package scot.mygov.publishing;

import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.util.JcrUtils;

import javax.jcr.*;

public class HippoUtils {

    private HippoUtils() {
        // utility class - prevent instantiation
    }

    public static Node getPublishedVariant(Node handle) throws RepositoryException {
        return getVariantWithState(handle, HippoStdNodeType.PUBLISHED);
    }

    public static Node getDraftVariant(Node handle) throws RepositoryException {
        return getVariantWithState(handle, HippoStdNodeType.DRAFT);
    }

    public static Node getVariantWithState(Node handle, String state) throws RepositoryException {
        String name = handle.getName();
        NodeIterator iterator = handle.getNodes(name);

        while (iterator.hasNext()) {
            Node variant = iterator.nextNode();
            String variantState = JcrUtils.getStringProperty(variant, HippoStdNodeType.HIPPOSTD_STATE, null);
            if (variantState.equals(state)) {
                return variant;
            }
        }
        return null;
    }

    public static void ensureHasMixin(Node node, String mixin) throws RepositoryException {
        if (!node.isNodeType(mixin)) {
            node.addMixin(mixin);
        }
    }

}
