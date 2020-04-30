package scot.mygov.publishing.linkprocessors;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Used by link processors to get a session.  This is useful for overriding when writing unit tests becuase Hippo
 * make use of a static member.
 */
interface SessionSource {

    Session getSession() throws RepositoryException;

}