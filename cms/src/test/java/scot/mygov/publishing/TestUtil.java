package scot.mygov.publishing;

import org.apache.commons.lang.NotImplementedException;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by z418868 on 10/10/2019.
 */
public class TestUtil {

    public static NodeIterator iterator(Collection<Node> nodes) {

        Iterator<Node> it = nodes.iterator();

        return new NodeIterator() {

            public boolean hasNext() {
                return  it.hasNext();
            }

            public Node nextNode() {
                return (Node) it.next();
            }

            public void skip(long skipNum) {
                throw new NotImplementedException();
            }

            public long getSize() {
                return nodes.size();
            }

            public long getPosition() {
                throw new NotImplementedException();
            }

            public NodeIterator next() {
                throw new NotImplementedException();
            }
        };
    };
}
