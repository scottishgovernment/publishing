package scot.gov.migration;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.UriInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.onehippo.forge.content.exim.core.ContentMigrationException;
import org.onehippo.forge.content.pojo.model.ContentNode;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MigrationResourceTest {

    @Test
    public void validationErrorThrownAs400Error() throws RepositoryException {
        Session daemonSession = daemonSession();
        MigrationUserCredentialsSource credentialsSource = mock(MigrationUserCredentialsSource.class);
        MigrationResource sut = new MigrationResource(daemonSession, credentialsSource);
        sut.documentUpdater = mock(DocumentUpdater.class);
        when(sut.documentUpdater.update(any(Session.class), anyString(), anyString(), eq(true), any(ContentNode.class), any(ContentAuthorship.class)))
                .thenThrow(new ContentMigrationException("invalid"));
        ContentNode node = mock(ContentNode.class);
        WebApplicationException ex = null;
        try {
            sut.newPublishingDocument(node, "site", "path", true, "", anyUriInfo());
        } catch (WebApplicationException e) {
            ex = e;
        } finally {
            Assert.assertEquals(ex.getResponse().getStatus(), 400);
        }
    }

    @Test
    public void repoErrorThrownAs500Error() throws Exception {
        Session daemonSession = exceptionThrowingfDaemonSession();
        MigrationUserCredentialsSource credentialsSource = mock(MigrationUserCredentialsSource.class);
        MigrationResource sut = new MigrationResource(daemonSession, credentialsSource);
        sut.documentUpdater = mock(DocumentUpdater.class);
        ContentNode node = mock(ContentNode.class);
        WebApplicationException ex = null;
        try {
            sut.newPublishingDocument(node, "site", "path", true, "auth", anyUriInfo());
        } catch (WebApplicationException e) {
            ex = e;
        } finally {
            Assert.assertEquals(ex.getResponse().getStatus(), 500);
        }
    }

    @Test
    public void greenpath() throws Exception {
        Session daemonSession = daemonSession();
        MigrationUserCredentialsSource credentialsSource = mock(MigrationUserCredentialsSource.class);
        MigrationResource sut = new MigrationResource(daemonSession, credentialsSource);
        sut.documentUpdater = mock(DocumentUpdater.class);
        when(sut.documentUpdater.update(any(Session.class), anyString(), anyString(), eq(true), any(ContentNode.class), any(ContentAuthorship.class))).thenReturn("path");
        ContentNode node = mock(ContentNode.class);
        WebApplicationException ex = null;
        MigrationResource.Result actual = sut.newPublishingDocument(node, "site", "path", true, "auth", anyUriInfo());
        Assert.assertEquals(actual.getPath(), "path");
    }

    Session daemonSession() throws RepositoryException {
        Session impersonatedSession = mock(Session.class);
        Session daemonSession = mock(Session.class);
        Mockito.when(daemonSession.impersonate(any())).thenReturn(impersonatedSession);
        return daemonSession;
    }

    Session exceptionThrowingfDaemonSession() throws RepositoryException {
        Session impersonatedSession = mock(Session.class);
        Session daemonSession = mock(Session.class);
        Mockito.when(daemonSession.impersonate(any())).thenThrow(new RepositoryException("arg"));
        return daemonSession;
    }

    UriInfo anyUriInfo() {
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap();
        map.put("createdBy", singletonList("createdBy"));
        map.put("created", singletonList("2010-01-01 00:00:00"));
        map.put("lastModifiedBy", singletonList("lastModifiedBy"));
        map.put("lastModified", singletonList("2010-01-01 00:00:00"));

        UriInfo info = Mockito.mock(UriInfo.class);
        when(info.getQueryParameters()).thenReturn(map);
        return info;
    }


}
