package scot.mygov.publishing.eventlisteners;

import org.apache.jackrabbit.value.StringValue;
import org.junit.Test;
import org.onehippo.cms7.event.HippoEvent;
import org.onehippo.forge.selection.hst.contentbean.ValueList;
import scot.mygov.publishing.HippoUtils;
import scot.mygov.publishing.test.TestUtil;

import javax.jcr.*;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AddEventListenerTest {

    @Test
    public void repoExceptionSwallowed() throws Exception {
        // ARRANGE
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        when(session.getNode(any())).thenThrow(new RepositoryException());
        AddEventListener sut = new AddEventListener(session, hippoUtils);
        HippoEvent event = new HippoEvent("app");
        event.set("action", "add");

        // ACT
        sut.handleEvent(event);

        // ASSERT - no exception is thrown
    }

    @Test
    public void ignoresNonAddEvent() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node folder = folderNode();
        when(session.getNode("path")).thenReturn(folder);
        AddEventListener sut = new AddEventListener(session, hippoUtils);
        HippoEvent event = eventWithAction("subtract").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(folder, never()).setProperty(eq("hippostd:foldertype"), any(String[].class));
    }

    @Test
    public void addsSlugForNewArticles() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node article = articleNode();
        Node migrationsNode = migrationsNode();
        when(article.getName()).thenReturn("name");
        when(session.getNode("path")).thenReturn(article);
        when(session.getNode("/content/migrations")).thenReturn(migrationsNode);

        AddEventListener sut = new AddEventListener(session, hippoUtils);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(article, never()).setProperty(eq("hippostd:foldertype"), any(String[].class));
        verify(article).setProperty(eq("publishing:slug"), eq("name"));
    }

    @Test
    public void addsOrgTagsForNewArticles() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node article = articleNode();
        Node migrationsNode = migrationsNode();
        when(article.getName()).thenReturn("name");
        when(session.getNode("path")).thenReturn(article);
        when(session.getNode("/content/migrations")).thenReturn(migrationsNode);

        AddEventListener sut = new AddEventListener(session, hippoUtils);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(article, never()).setProperty(eq("hippostd:foldertype"), any(String[].class));
        verify(article).setProperty(eq("publishing:slug"), eq("name"));
    }

    @Test
    public void ignoresAddNonFolderEvent() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node folder = nonFolderNode();
        when(session.getNode("path")).thenReturn(folder);
        AddEventListener sut = new AddEventListener(session, hippoUtils);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(folder, never()).setProperty(eq("hippostd:foldertype"), any(String[].class));
    }

    @Test
    public void setsActionsForCategoryLessThan10Deep() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node index = mock(Node.class);
        Node folder = folderNode(index);
        when(folder.getDepth()).thenReturn(5);
        Value [] values = new Value[] { new StringValue("new-publishing-category")};
        Property folderTypeProperty = folderTypeProperty(values);
        when(folder.getProperty("hippostd:foldertype")).thenReturn(folderTypeProperty);
        when(session.getNode("path")).thenReturn(folder);
        AddEventListener sut = new AddEventListener(session, hippoUtils);
        organisationTags(session, index, sut);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(folder).setProperty("hippostd:foldertype", allActions());
    }

    @Test
    public void setsActionsForCategory9Deep() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node index = mock(Node.class);
        Node folder = folderNode(index);
        when(folder.getDepth()).thenReturn(12); // the + 3 for the part of the path before the site.
        Value [] values = new Value[] { new StringValue("new-publishing-category")};
        Property folderTypeProperty = folderTypeProperty(values);
        when(folder.getProperty("hippostd:foldertype")).thenReturn(folderTypeProperty);
        when(session.getNode("path")).thenReturn(folder);
        AddEventListener sut = new AddEventListener(session, hippoUtils);
        HippoEvent event = eventWithAction("add").result("path");
        organisationTags(session, index, sut);

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(folder).setProperty("hippostd:foldertype", allActions());
    }

    Property folderTypeProperty(Value[] values) throws RepositoryException {
        Property property = mock(Property.class);
        when(property.getValues()).thenReturn(values);
        return property;
    }

    @Test
    public void setsActionsForCategory10Deep() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node index = mock(Node.class);
        Node folder = folderNode(index);
        when(folder.getDepth()).thenReturn(13); // the + 3 for the part of the path before the site.
        when(session.getNode("path")).thenReturn(folder);
        Value [] values = new Value[] { new StringValue("new-publishing-category")};
        Property folderTypeProperty = folderTypeProperty(values);
        when(folder.getProperty("hippostd:foldertype")).thenReturn(folderTypeProperty);
        AddEventListener sut = new AddEventListener(session, hippoUtils);
        organisationTags(session, index, sut);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(folder).setProperty("hippostd:foldertype", actionsWithoutAddCategory());
    }

    @Test
    public void setsActionsForCategoryMoreThan10Deep() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node index = mock(Node.class);
        Node folder = folderNode(index);
        when(folder.getDepth()).thenReturn(14); // the + 3 for the part of the path before the site.
        when(session.getNode("path")).thenReturn(folder);
        Value [] values = new Value[] { new StringValue("new-publishing-category")};
        Property folderTypeProperty = folderTypeProperty(values);
        when(folder.getProperty("hippostd:foldertype")).thenReturn(folderTypeProperty);
        AddEventListener sut = new AddEventListener(session, hippoUtils);
        organisationTags(session, index, sut);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(folder).setProperty("hippostd:foldertype", actionsWithoutAddCategory());
    }

    @Test
    public void setsNavigationStyleToListIfDeeperThan1() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node index = mock(Node.class);
        Node folder = folderNode(index);
        when(folder.getDepth()).thenReturn(10); // the + 3 for the part of the path before the site.
        when(session.getNode("path")).thenReturn(folder);
        Value [] values = new Value[] { new StringValue("new-publishing-category")};
        Property folderTypeProperty = folderTypeProperty(values);
        when(folder.getProperty("hippostd:foldertype")).thenReturn(folderTypeProperty);

        AddEventListener sut = new AddEventListener(session, hippoUtils);
        organisationTags(session, index, sut);

        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(index).setProperty("publishing:navigationType", "list");
    }

    @Test
    public void setsNavigationStyleToListIf1Deep() throws RepositoryException {
        // ARRANGE
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node index = mock(Node.class);
        Node folder = folderNode(index);
        when(folder.getDepth()).thenReturn(4); // the + 3 for the part of the path before the site.
        when(session.getNode("path")).thenReturn(folder);
        Value [] values = new Value[] { new StringValue("new-publishing-category")};
        Property folderTypeProperty = folderTypeProperty(values);
        when(folder.getProperty("hippostd:foldertype")).thenReturn(folderTypeProperty);
        AddEventListener sut = new AddEventListener(session, hippoUtils);
        organisationTags(session, index, sut);
        HippoEvent event = eventWithAction("add").result("path");

        // ACT
        sut.handleEvent(event);

        // ASSERT
        verify(index).setProperty("publishing:navigationType", "grid");
    }

    @Test
    public void setsActionsForGuide() throws RepositoryException {
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node index = mock(Node.class);
        Node folder = folderNode(index);
        Value [] values = new Value[] { new StringValue("new-publishing-guide-page")};
        Property folderTypeProperty = folderTypeProperty(values);
        when(folder.getProperty("hippostd:foldertype")).thenReturn(folderTypeProperty);

        AddEventListener sut = new AddEventListener(session, hippoUtils);
        organisationTags(session, index, sut);
        HippoEvent event = eventWithAction("add").result("path");
        when(session.getNode(event.result())).thenReturn(folder);

        sut.handleEvent(event);

    }

    @Test
    public void nothingWhenNotUnderDocuments() throws RepositoryException {
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node index = mock(Node.class);
        Node folder = folderNodeWithPath(index, "/some/other/path");
        Value [] values = new Value[] { new StringValue("new-publishing-guide-page")};
        Property folderTypeProperty = folderTypeProperty(values);
        when(folder.getProperty("hippostd:foldertype")).thenReturn(folderTypeProperty);

        AddEventListener sut = new AddEventListener(session, hippoUtils);
        organisationTags(session, index, sut);
        HippoEvent event = eventWithAction("add").result("path");
        when(session.getNode(event.result())).thenReturn(folder);
        sut.handleEvent(event);

    }

    @Test
    public void setsOrganisationsWhenRequired() throws RepositoryException {
        Session session = mock(Session.class);
        HippoUtils hippoUtils = mock(HippoUtils.class);
        Node index = mock(Node.class);
        AddEventListener sut = new AddEventListener(session, hippoUtils);
        organisationTags(session, index, sut);
        HippoEvent event = eventWithAction("add").result("path");
        when(session.getNode(event.result())).thenReturn(index);
        when(index.hasProperty("publishing:organisationtags")).thenReturn(true);
        sut.handleEvent(event);
    }

    String [] allActions() {
        return new String [] {"new-publishing-article", "new-publishing-category", "new-publishing-guide", "new-publishing-mirror", "new-publishing-formbase", "new-publishing-fairrent", "new-publishing-smartanswer", "new-publishing-stepbystepguide", "new-publishing-documentcoverpage", "new-publishing-documents-folder", "new-fragment-folder"};
    }

    String [] actionsWithoutAddCategory() {
        return new String [] {"new-publishing-article", "new-publishing-guide", "new-publishing-mirror", "new-publishing-formbase", "new-publishing-fairrent", "new-publishing-smartanswer", "new-publishing-stepbystepguide", "new-publishing-documentcoverpage", "new-publishing-documents-folder", "new-fragment-folder"};
    }

    String [] actionsForGuide() {
        return new String [] {"new-publishing-guide-page"};
    }

    HippoEvent eventWithAction(String action) {
        HippoEvent event = new HippoEvent("app");
        event.action(action);
        return event;
    }
    Node folderNode(Node index) throws RepositoryException {
        return folderNodeWithPath(index, "/content/documents/test");
    }

    Node folderNodeWithPath(Node index, String path) throws RepositoryException {
        Node folder = mock(Node.class);
        Node indexHandle = mock(Node.class);
        when(folder.isNodeType("hippostd:folder")).thenReturn(true);
        when(folder.isNodeType("publishing:base")).thenReturn(false);
        when(folder.getPath()).thenReturn(path);
        when(indexHandle.getNodes()).thenReturn(TestUtil.iterator(singletonList(index)));
        when(folder.getNode("index")).thenReturn(indexHandle);
        when(folder.getNode("index").getNode("index")).thenReturn(index);
        return folder;
    }

    Node folderNode() throws RepositoryException {
        Node index = mock(Node.class);
        return folderNode(index);
    }

    Node nonFolderNode() throws RepositoryException {
        Node folder = mock(Node.class);
        when(folder.isNodeType("hippostd:folder")).thenReturn(false);
        when(folder.isNodeType("publishing:base")).thenReturn(false);
        return folder;
    }

    Node articleNode() throws RepositoryException {
        Node article = mock(Node.class);
        when(article.isNodeType("publishing:base")).thenReturn(true);
        when(article.isNodeType("publishing:article")).thenReturn(true);
        when(article.hasProperty("publishing:slug")).thenReturn(true);
        return article;
    }

    Node migrationsNode() throws RepositoryException {
        Node node = mock(Node.class);
        Property p = mock(Property.class);
        when(p.getBoolean()).thenReturn(false);
        when(node.getProperty("running")).thenReturn(p);
        return node;
    }

    void organisationTags(Session session, Node node, AddEventListener eventListener) throws RepositoryException {
        Node groups = mock(Node.class);
        when(session.getNode("/hippo:configuration/hippo:groups/")).thenReturn(groups);
        NodeIterator iterator = mock(NodeIterator.class);
        when(groups.getNodes()).thenReturn(iterator);
        Property property = mock(Property.class);
        Value value = mock(Value.class);
        when(node.getProperty("hippostdpubwf:createdBy")).thenReturn(property);
        when(property.getValue()).thenReturn(value);
        when(value.getString()).thenReturn("user");
        ValueList valueList = mock(ValueList.class);
        when(eventListener.hippoUtils.getValueList(
                session, "/content/documents/publishing/valuelists/organisationtags")).thenReturn(valueList);
        when(valueList.getItems()).thenReturn(null);
    }

}
