package scot.gov.migration;

import javax.jcr.*;

public class HippoUtils {


    public void apply(NodeIterator it, ThrowingConsumer consumer) throws RepositoryException {
        while (it.hasNext()) {
            Node node = it.nextNode();
            consumer.accept(node);
        }
    }

    @FunctionalInterface
    public interface ThrowingConsumer {
        void accept(Node t) throws RepositoryException;
    }

    public Node createNode(Node parent, String name, String primaryType, String ...mixins)
            throws RepositoryException {

        Node node = parent.addNode(name, primaryType);
        for (String mixin : mixins) {
            node.addMixin(mixin);
        }
        return node;
    }
}

