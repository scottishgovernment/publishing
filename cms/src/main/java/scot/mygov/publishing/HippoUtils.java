package scot.mygov.publishing;

import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.manager.ObjectConverter;
import org.hippoecm.hst.site.HstServices;
import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.forge.selection.hst.contentbean.ValueList;

import javax.jcr.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HippoUtils {

    public Node getPublishedVariant(Node handle) throws RepositoryException {
        return getVariantWithState(handle, HippoStdNodeType.PUBLISHED);
    }

    public Node getDraftVariant(Node handle) throws RepositoryException {
        return getVariantWithState(handle, HippoStdNodeType.DRAFT);
    }

    public Node getVariantWithState(Node handle, String state) throws RepositoryException {
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

    public void ensureHasMixin(Node node, String mixin) throws RepositoryException {
        if (!node.isNodeType(mixin)) {
            node.addMixin(mixin);
        }
    }

    public boolean isOneOfNodeTypes(Node node, String ...nodeTypes) throws RepositoryException {
        Set<String> nodeTypesSet = new HashSet<>();
        Collections.addAll(nodeTypesSet, nodeTypes);
        for (String type : nodeTypesSet) {
            if (node.isNodeType(type)) {
                return true;
            }
        }
        return false;
    }

    public void apply(NodeIterator it, ThrowingConsumer consumer) throws RepositoryException {
        apply(it, node -> true, consumer);
    }

    public void apply(NodeIterator it, ThrowingPredicate predicate, ThrowingConsumer consumer) throws RepositoryException {
        while (it.hasNext()) {
            Node node = it.nextNode();
            if (predicate.test(node)) {
                consumer.accept(node);
            }
        }
    }

    public ValueList getValueList(Session session, String name) throws RepositoryException {
        try {
            ObjectConverter converter = HstServices.getComponentManager().getComponent(ObjectConverter.class);
            return (ValueList) converter.getObject(session, name);
        } catch (ObjectBeanManagerException e) {
            throw new RepositoryException("Issues retrieving the ValueList", e);
        }
    }

    @FunctionalInterface
    public interface ThrowingPredicate {
        boolean test(Node t) throws RepositoryException;
    }

    @FunctionalInterface
    public interface ThrowingConsumer {
        void accept(Node t) throws RepositoryException;
    }
}
