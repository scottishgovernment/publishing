package scot.mygov.publishing.eventlisteners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class EventListerUtil {

    private static final Logger LOG = LoggerFactory.getLogger(EventListerUtil.class);

    static void ensureRefreshFalse(Session session) {

        try {
            session.refresh(false);
        } catch (RepositoryException e) {
            LOG.error("Failed to call session.refresh(false)", e);
        }
    }
}
