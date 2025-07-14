package scot.mygov.publishing.eventlisteners;

import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.api.HippoNode;
import org.junit.Test;
import org.onehippo.repository.events.HippoWorkflowEvent;
import scot.mygov.publishing.test.TestUtil;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.Collections;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static scot.mygov.publishing.eventlisteners.MirrorEventListener.PUBLISH_INTERACTION;

/**
 * Created by z418868 on 10/03/2020.
 */
public class MirrorEventListenerTest {

    @Test
    public void repoExceptionHandledCorrectly() throws RepositoryException {

        // ARRANGE
        Session session = exceptionThrowingSession();
        MirrorEventListener sut = new MirrorEventListener(session);

        // ACT
        sut.handleEvent(publishEvent());

        // ASSERT - the sesion is never saved, no changes made
        verify(session, never()).save();
    }

    @Test
    public void ignoresUnsuccessfulEvents() throws RepositoryException {

        // ARRANGE
        Session session = anySession();
        MirrorEventListener sut = new MirrorEventListener(session);

        // ACT
        sut.handleEvent(failedPublishEvent());

        // ASSERT - the sesion is never saved, no changes made
        verify(session, never()).save();
    }

    @Test
    public void ignoresNonPublishEvents() throws RepositoryException {

        // ARRANGE
        Session session = anySession();
        MirrorEventListener sut = new MirrorEventListener(session);

        // ACT
        sut.handleEvent(nonPublishEvent());

        // ASSERT - the sesion is never saved, no changes made
        verify(session, never()).save();
    }

    @Test
    public void handlesNoPublishedVariantPresent() throws RepositoryException {
        // ARRANGE
        Session session = sessionWithNoPublishedVariant();

        // session.getNodeByIdentifier("subjectId")
        // Node handelWithNoPublushedVariabt
        MirrorEventListener sut = new MirrorEventListener(session);

        // ACT
        sut.handleEvent(publishEvent());

        // ASSERT - the sesion is never saved, no changes made
        verify(session, never()).save();
    }

    @Test
    public void ignoresPublishOfNonMirror() throws RepositoryException {
        // ARRANGE
        Session session = sessionWithNonMirror();

        // session.getNodeByIdentifier("subjectId")
        // Node handelWithNoPublushedVariabt
        MirrorEventListener sut = new MirrorEventListener(session);

        // ACT
        sut.handleEvent(publishEvent());

        // ASSERT - the sesion is never saved, no changes made
        verify(session, never()).save();
    }

    @Test
    public void updatesNameForSuccessfulPublishOfMirror() throws RepositoryException {
        // ARRANGE
        Session session = sessionWithMirror();
        MirrorEventListener sut = new MirrorEventListener(session);

        // ACT
        sut.handleEvent(publishEvent());

        // ASSERT - the sesion is never saved, no changes made
        verify(session, times(1)).save();
    }

    Session anySession() throws RepositoryException {
        Session session = mock(Session.class);
        return session;
    }

    Session exceptionThrowingSession() throws RepositoryException {
        Session session = mock(Session.class);
        when(session.getNodeByIdentifier(any())).thenThrow(new RepositoryException(""));
        return session;
    }

    Session sessionWithNoPublishedVariant() throws RepositoryException {
        Session session = mock(Session.class);
        HippoNode node = mock(HippoNode.class);
        when(node.getName()).thenReturn("name");
        when(node.getNodes("name")).thenReturn(TestUtil.iterator(Collections.emptyList()));
        when(session.getNodeByIdentifier("subjectId")).thenReturn(node);
        return session;
    }

    Session sessionWithNonMirror() throws RepositoryException {
        Session session = mock(Session.class);
        HippoNode node = mock(HippoNode.class);
        Node publishedVariant = mock(Node.class);
        Property property = stringProperty("published");
        when(publishedVariant.hasProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(true);
        when(publishedVariant.getProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(property);
        when(publishedVariant.isNodeType("publishing:mirror")).thenReturn(false);
        when(node.getName()).thenReturn("name");
        when(node.getNodes("name")).thenReturn(TestUtil.iterator(singletonList(publishedVariant)));
        when(session.getNodeByIdentifier("subjectId")).thenReturn(node);
        return session;
    }

    Session sessionWithMirror() throws RepositoryException {
        Session session = mock(Session.class);
        HippoNode node = mock(HippoNode.class);
        when(node.getName()).thenReturn("name");

        Node publishedVariant = mock(Node.class);
        when(publishedVariant.getParent()).thenReturn(node);
        when(node.getNodes()).thenReturn(TestUtil.iterator(singletonList(publishedVariant)));
        Property state = stringProperty("published");
        when(publishedVariant.hasProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(true);
        when(publishedVariant.getProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(state);
        when(publishedVariant.isNodeType("publishing:mirror")).thenReturn(true);
        when(node.getNodes("name")).thenReturn(TestUtil.iterator(singletonList(publishedVariant)));

        Node documentNode = mock(Node.class);
        when(publishedVariant.getNode("publishing:document")).thenReturn(documentNode);

        Property docbase = stringProperty("pointedAt");
        when(documentNode.getProperty("hippo:docbase")).thenReturn(docbase);

        Node pointedAt = mock(Node.class);
        when(pointedAt.getName()).thenReturn("name");
        Node draft = mock(Node.class);
        when(pointedAt.getNodes("name")).thenReturn(TestUtil.iterator(singletonList(draft)));
        Property draftState = stringProperty(HippoStdNodeType.DRAFT);
        when(draft.hasProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(true);
        when(draft.getProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(draftState);
        Property title = stringProperty("title");
        when(draft.getProperty("publishing:title")).thenReturn(title);
        when(session.getNodeByIdentifier("subjectId")).thenReturn(node);
        when(session.getNodeByIdentifier("pointedAt")).thenReturn(pointedAt);
        return session;
    }

    Property stringProperty(String val) throws RepositoryException {
        Property property = mock(Property.class);
        when(property.getString()).thenReturn(val);
        return property;
    }

    HippoWorkflowEvent publishEvent() {
        return event(true, PUBLISH_INTERACTION);
    }

    HippoWorkflowEvent nonPublishEvent() {
        return event(true, "default:handle:somthingelse");
    }

    HippoWorkflowEvent failedPublishEvent() {
        return event(false, PUBLISH_INTERACTION);
    }

    HippoWorkflowEvent event(boolean success, String interaction) {
        HippoWorkflowEvent event = mock(HippoWorkflowEvent.class);
        when(event.success()).thenReturn(success);
        when(event.interaction()).thenReturn(interaction);
        when(event.subjectId()).thenReturn("subjectId");
        return event;
    }
}
