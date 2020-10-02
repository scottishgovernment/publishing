package scot.mygov.publishing.advancedsearch.columns;

import org.apache.wicket.model.IDetachable;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.model.event.IObservable;
import org.hippoecm.frontend.model.event.IObservationContext;
import org.hippoecm.frontend.model.event.Observable;
import org.hippoecm.repository.api.HippoNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.Calendar;

public class CustomAttributes implements IObservable, IDetachable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(CustomAttributes.class);

    private JcrNodeModel nodeModel;
    private Observable observable;
    private transient boolean loaded = false;

    private transient Calendar reviewDate;
    private transient Calendar lastOfficialUpdateDate;

    public CustomAttributes(JcrNodeModel nodeModel) {
        this.nodeModel = nodeModel;
        observable = new Observable(nodeModel);
    }

    public Calendar getReviewDate() {
        load();
        return reviewDate;
    }

    public Calendar getLastOfficialUpdateDate() {
        load();
        return lastOfficialUpdateDate;
    }

    public void detach() {
        loaded = false;

        reviewDate = null;
        lastOfficialUpdateDate = null;

        nodeModel.detach();
        observable.detach();
    }

    void load() {
        if (loaded) {
            return;
        }

        observable.setTarget(null);
        final Node node = nodeModel.getNode();
        if (node == null) {
            return;
        }

        try {
            if (node.isNodeType(HippoNodeType.NT_HANDLE)) {
                retrieveProperties(node.getNodes(node.getName()));
            }
        } catch (RepositoryException repositoryException) {
            try {
                LOG.error("Unable to obtain state properties, nodeModel path: " + node.getPath(), repositoryException);
            } catch (RepositoryException nodeModelPathException) {
                LOG.error("Unable to obtain state properties", repositoryException);
                LOG.error("Unable to get path of node model", nodeModelPathException);
            }
        }
        loaded = true;
    }

    private void retrieveProperties(final NodeIterator variants) throws RepositoryException {
        Node variant;
        while (variants.hasNext()) {
            variant = variants.nextNode();
            if(variant.hasProperty("publishing:lastUpdatedDate")) {
                lastOfficialUpdateDate = variant.getProperty("publishing:lastUpdatedDate").getDate();
            }
            if(variant.hasProperty("publishing:reviewDate")) {
                reviewDate = variant.getProperty("publishing:reviewDate").getDate();
            }
        }
    }

    public void setObservationContext(IObservationContext<? extends IObservable> context) {
        observable.setObservationContext(context);
    }

    public void startObservation() {
        observable.startObservation();
    }

    public void stopObservation() {
        observable.stopObservation();
    }

}
