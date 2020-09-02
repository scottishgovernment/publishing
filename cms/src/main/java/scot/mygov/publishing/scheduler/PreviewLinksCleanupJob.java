package scot.mygov.publishing.scheduler;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.repository.scheduling.RepositoryJob;
import org.onehippo.repository.scheduling.RepositoryJobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreviewLinksCleanupJob implements RepositoryJob {

    private static final Logger log = LoggerFactory.getLogger(PreviewLinksCleanupJob.class);

    private static final String CONFIG_BATCH_SIZE = "batchsize";
    private static final String PREVIEW_LINKS_QUERY = "//element(*, staging:preview)";

    @Override
    public void execute(final RepositoryJobExecutionContext context) throws RepositoryException {
        log.info("Running preview links cleanup job");
        final Session session = context.createSystemSession();
        try {
            long batchSize;
            try {
                batchSize = Long.parseLong(context.getAttribute(CONFIG_BATCH_SIZE));
            } catch (NumberFormatException e) {
                log.warn("Incorrect batch size '"+context.getAttribute(CONFIG_BATCH_SIZE)+"'. Setting default to 100");
                batchSize = 100;
            }
            removeOldFormData(batchSize, session);
        } finally {
            session.logout();
        }
    }

    private void removeOldFormData(final long batchSize, final Session session) throws RepositoryException {
        final QueryManager queryManager = session.getWorkspace().getQueryManager();
        final Query query = queryManager.createQuery(PREVIEW_LINKS_QUERY, Query.XPATH);
        final NodeIterator nodes = query.execute().getNodes();
        int count = 0;

        while (nodes.hasNext()) {
            try {
                final Node node = nodes.nextNode();
                Calendar expirationCalendar = JcrUtils.getDateProperty(node, "staging:expirationdate", null);
                if(expirationCalendar == null || Calendar.getInstance().before(expirationCalendar)){
                    break;
                }
                log.debug("Removing preview node at {}", node.getPath());
                node.remove();
                if (count++ % batchSize == 0) {
                    session.save();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
            } catch (RepositoryException e) {
                log.error("Error while cleaning up preview links", e);
            }
        }
        if (session.hasPendingChanges()) {
            session.save();
        }
        if (count > 0) {
            log.info("Done cleaning " + count + " items");
        } else {
            log.info("No timed out items");
        }
    }

}
