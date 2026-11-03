package scot.mygov.publishing.channelevents;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.pagecomposer.jaxrs.api.PageCreateContext;
import org.hippoecm.hst.pagecomposer.jaxrs.api.PageCreateEvent;
import org.hippoecm.hst.pagecomposer.jaxrs.services.exceptions.ClientException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import scot.gov.publishing.sluglookup.SlugLookupPaths;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewPageSlugValidationListenerTest {

    NewPageSlugValidationListener sut = new NewPageSlugValidationListener();

    @Test
    public void onPageCreateEventDoesNothingIfEventAlreadyHasException() {
        // ARRANGE
        PageCreateEvent event = mock(PageCreateEvent.class);
        when(event.getException()).thenReturn(new RuntimeException("already failed"));

        // ACT
        sut.onPageCreateEvent(event);

        // ASSERT
        verify(event, never()).getPageActionContext();
    }

    @Test
    public void onPageCreateEventDoesNothingIfNoNewSiteMapItemNode() {
        // ARRANGE
        PageCreateEvent event = mock(PageCreateEvent.class);
        PageCreateContext context = mock(PageCreateContext.class);
        when(event.getPageActionContext()).thenReturn(context);
        when(context.getNewSiteMapItemNode()).thenReturn(null);

        // ACT
        sut.onPageCreateEvent(event);

        // ASSERT
        verify(event, never()).setException(any());
    }

    @Test
    public void onPageCreateEventMessageIncludesClashingContentPath() throws Exception {
        // ARRANGE
        PageCreateEvent event = mock(PageCreateEvent.class);
        PageCreateContext context = mock(PageCreateContext.class);
        Node newSiteMapItemNode = mock(Node.class);
        Mount mount = mock(Mount.class);
        HstRequestContext requestContext = mock(HstRequestContext.class);
        String lookupPath = SlugLookupPaths.slugLookupPath("who-we-are", "foodcommission", "global", "live");
        Session session = sessionWithLookupNodeAt(lookupPath, "/content/documents/foodcommission/browse/who-we-are");

        when(event.getPageActionContext()).thenReturn(context);
        when(context.getNewSiteMapItemNode()).thenReturn(newSiteMapItemNode);
        when(newSiteMapItemNode.getName()).thenReturn("who-we-are");
        when(context.getEditingMount()).thenReturn(mount);
        when(mount.getContentPath()).thenReturn("/content/documents/foodcommission");
        when(context.getRequestContext()).thenReturn(requestContext);
        when(requestContext.getSession()).thenReturn(session);

        // ACT
        sut.onPageCreateEvent(event);

        // ASSERT
        ArgumentCaptor<ClientException> captor = ArgumentCaptor.forClass(ClientException.class);
        verify(event).setException(captor.capture());
        assertTrue(captor.getValue().getMessage().contains("/content/documents/foodcommission/browse/who-we-are"));
    }

    @Test
    public void onPageCreateEventMessageFallsBackWhenLookupNodeHasNoPathProperty() throws Exception {
        // ARRANGE - a stale lookup node that exists but has no sluglookup:path property
        PageCreateEvent event = mock(PageCreateEvent.class);
        PageCreateContext context = mock(PageCreateContext.class);
        Node newSiteMapItemNode = mock(Node.class);
        Mount mount = mock(Mount.class);
        HstRequestContext requestContext = mock(HstRequestContext.class);
        String lookupPath = SlugLookupPaths.slugLookupPath("who-we-are", "foodcommission", "global", "live");
        Session session = sessionWithLookupNodeAt(lookupPath, null);

        when(event.getPageActionContext()).thenReturn(context);
        when(context.getNewSiteMapItemNode()).thenReturn(newSiteMapItemNode);
        when(newSiteMapItemNode.getName()).thenReturn("who-we-are");
        when(context.getEditingMount()).thenReturn(mount);
        when(mount.getContentPath()).thenReturn("/content/documents/foodcommission");
        when(context.getRequestContext()).thenReturn(requestContext);
        when(requestContext.getSession()).thenReturn(session);

        // ACT
        sut.onPageCreateEvent(event);

        // ASSERT
        ArgumentCaptor<ClientException> captor = ArgumentCaptor.forClass(ClientException.class);
        verify(event).setException(captor.capture());
        assertEquals("Slug 'who-we-are' clashes with the slug of an existing page", captor.getValue().getMessage());
    }

    @Test
    public void onPageCreateEventDoesNotSetExceptionWhenNoClash() throws Exception {
        // ARRANGE
        PageCreateEvent event = mock(PageCreateEvent.class);
        PageCreateContext context = mock(PageCreateContext.class);
        Node newSiteMapItemNode = mock(Node.class);
        Mount mount = mock(Mount.class);
        HstRequestContext requestContext = mock(HstRequestContext.class);
        Session session = sessionWithNoLookupNodes();

        when(event.getPageActionContext()).thenReturn(context);
        when(context.getNewSiteMapItemNode()).thenReturn(newSiteMapItemNode);
        when(newSiteMapItemNode.getName()).thenReturn("new-page");
        when(context.getEditingMount()).thenReturn(mount);
        when(mount.getContentPath()).thenReturn("/content/documents/foodcommission");
        when(context.getRequestContext()).thenReturn(requestContext);
        when(requestContext.getSession()).thenReturn(session);

        // ACT
        sut.onPageCreateEvent(event);

        // ASSERT
        verify(event, never()).setException(any());
    }

    @Test
    public void slugLookupClashReturnsNullWhenNeitherMountHasALookupNode() throws Exception {
        // ARRANGE
        Session session = sessionWithNoLookupNodes();

        // ACT
        String actual = sut.slugLookupClash(session, "foodcommission", "new-page");

        // ASSERT
        assertNull(actual);
    }

    @Test
    public void slugLookupClashReturnsLiveLookupPathWhenItExists() throws Exception {
        // ARRANGE
        String livePath = SlugLookupPaths.slugLookupPath("who-we-are", "foodcommission", "global", "live");
        Session session = sessionWithLookupNodeAt(livePath, "/content/documents/foodcommission/browse/who-we-are");

        // ACT
        String actual = sut.slugLookupClash(session, "foodcommission", "who-we-are");

        // ASSERT
        assertEquals(livePath, actual);
    }

    @Test
    public void slugLookupClashReturnsPreviewLookupPathWhenOnlyThatExists() throws Exception {
        // ARRANGE
        String previewPath = SlugLookupPaths.slugLookupPath("who-we-are", "foodcommission", "global", "preview");
        Session session = sessionWithLookupNodeAt(previewPath, "/content/documents/foodcommission/browse/who-we-are");

        // ACT
        String actual = sut.slugLookupClash(session, "foodcommission", "who-we-are");

        // ASSERT
        assertEquals(previewPath, actual);
    }

    @Test
    public void clashingContentPathReturnsPropertyValueWhenPresent() throws Exception {
        // ARRANGE
        Session session = mock(Session.class);
        Node lookupNode = mock(Node.class);
        Property property = mock(Property.class);
        when(session.getNode("/lookup/path")).thenReturn(lookupNode);
        when(lookupNode.hasProperty("sluglookup:path")).thenReturn(true);
        when(lookupNode.getProperty("sluglookup:path")).thenReturn(property);
        when(property.getString()).thenReturn("/content/documents/foodcommission/browse/who-we-are");

        // ACT
        String actual = sut.clashingContentPath(session, "/lookup/path");

        // ASSERT
        assertEquals("/content/documents/foodcommission/browse/who-we-are", actual);
    }

    @Test
    public void clashingContentPathReturnsNullWhenPropertyMissing() throws Exception {
        // ARRANGE
        Session session = mock(Session.class);
        Node lookupNode = mock(Node.class);
        when(session.getNode("/lookup/path")).thenReturn(lookupNode);
        when(lookupNode.hasProperty("sluglookup:path")).thenReturn(false);

        // ACT
        String actual = sut.clashingContentPath(session, "/lookup/path");

        // ASSERT
        assertNull(actual);
    }

    Session sessionWithNoLookupNodes() throws Exception {
        Session session = mock(Session.class);
        when(session.nodeExists(any())).thenReturn(false);
        return session;
    }

    Session sessionWithLookupNodeAt(String path, String contentPath) throws Exception {
        Session session = mock(Session.class);
        when(session.nodeExists(any())).thenReturn(false);
        when(session.nodeExists(eq(path))).thenReturn(true);

        Node lookupNode = mock(Node.class);
        when(session.getNode(eq(path))).thenReturn(lookupNode);
        if (contentPath != null) {
            Property property = mock(Property.class);
            when(lookupNode.hasProperty("sluglookup:path")).thenReturn(true);
            when(lookupNode.getProperty("sluglookup:path")).thenReturn(property);
            when(property.getString()).thenReturn(contentPath);
        } else {
            when(lookupNode.hasProperty("sluglookup:path")).thenReturn(false);
        }

        return session;
    }
}
