package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedMount;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.hippoecm.repository.HippoStdNodeType;
import org.junit.Test;
import scot.mygov.publishing.TestUtil;
import scot.mygov.publishing.beans.Base;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Created by z418868 on 25/10/2019.
 */
public class GoogleTagManagerComponentTest {

    @Test
    public void setsExpectedValuesFromGtmPath() throws RepositoryException {

        // ARRANGE
        GoogleTagManagerComponent sut = new GoogleTagManagerComponent();
        HstRequest request = request("expectedGtmName", "expectedGtmId", "expectedContainerId", "expectedGtmAuth", "expectedGtmEnv");
        HstResponse response = mock(HstResponse.class);

        // ACT
        sut.doBeforeRender(request, response);

        // ASSERT
        verify(request).setAttribute("gtmName", "expectedGtmName");
        verify(request).setAttribute("gtmId", "expectedGtmId");
        verify(request).setAttribute("gtmContainerId", "expectedContainerId");
        verify(request).setAttribute("gtmAuth", "expectedGtmAuth");
        verify(request).setAttribute("gtmEnv", "expectedGtmEnv");
    }

    @Test
    public void repExceptionResultsInEmptyValues() throws RepositoryException {

        // ARRANGE
        GoogleTagManagerComponent sut = new GoogleTagManagerComponent();
        HstRequest request = exceptionThrowingRequest("expectedGtmName", "expectedGtmId");
        HstResponse response = mock(HstResponse.class);

        // ACT
        sut.doBeforeRender(request, response);

        // ASSERT
        verify(request).setAttribute("gtmContainerId", "");
        verify(request).setAttribute("gtmAuth", "");
        verify(request).setAttribute("gtmEnv", "");
    }

    @Test
    public void noGtmPathOnMountResultsInEmptyValues() throws RepositoryException {

        // ARRANGE
        GoogleTagManagerComponent sut = new GoogleTagManagerComponent();
        HstRequest request = requestWithGtmPath(null);
        HstResponse response = mock(HstResponse.class);

        // ACT
        sut.doBeforeRender(request, response);

        // ASSERT
        verify(request).setAttribute("gtmContainerId", "");
        verify(request).setAttribute("gtmAuth", "");
        verify(request).setAttribute("gtmEnv", "");
    }

    @Test
    public void gtmPathPointingAtNonExistantPathResultsInEmptyValues() throws RepositoryException {

        // ARRANGE
        GoogleTagManagerComponent sut = new GoogleTagManagerComponent();
        Session session = mock(Session.class);
        HstRequest request = request("", "", session);
        HstResponse response = mock(HstResponse.class);

        // ACT
        sut.doBeforeRender(request, response);

        // ASSERT
        verify(request).setAttribute("gtmContainerId", "");
        verify(request).setAttribute("gtmAuth", "");
        verify(request).setAttribute("gtmEnv", "");
    }

    HstRequest request(String configName, String pathInfo, String containerId, String auth, String env) throws RepositoryException {
        Session session = sessionWithValues(containerId, auth, env);
        return request(configName, pathInfo, session);
    }

    HstRequest request(String configName, String pathInfo, Session session) throws RepositoryException {
        HstRequest request = mock(HstRequest.class);

        HstRequestContext context = mock(HstRequestContext.class);
        when(context.getSession()).thenReturn(session);
        when(request.getRequestContext()).thenReturn(context);

        ResolvedSiteMapItem resolvedSiteMapItem = mock(ResolvedSiteMapItem.class);
        when(context.getResolvedSiteMapItem()).thenReturn(resolvedSiteMapItem);
        when(resolvedSiteMapItem.getPathInfo()).thenReturn(pathInfo);

        HstComponentConfiguration hstComponentConfiguration = mock(HstComponentConfiguration.class);
        when(resolvedSiteMapItem.getHstComponentConfiguration()).thenReturn(hstComponentConfiguration);
        when(hstComponentConfiguration.getName()).thenReturn(configName);

        ResolvedMount resolvedMount = mock(ResolvedMount.class);
        when(resolvedSiteMapItem.getResolvedMount()).thenReturn(resolvedMount);

        Mount mount = mock(Mount.class);
        when(resolvedMount.getMount()).thenReturn(mount);
        when(mount.getProperty("publishing:gtm")).thenReturn("gtmPath");

        return request;
    }

    HstRequest requestWithGtmPath(String gtmPath) throws RepositoryException {
        HstRequest request = mock(HstRequest.class);

        HstRequestContext context = mock(HstRequestContext.class);
        when(request.getRequestContext()).thenReturn(context);

        ResolvedSiteMapItem resolvedSiteMapItem = mock(ResolvedSiteMapItem.class);
        when(context.getResolvedSiteMapItem()).thenReturn(resolvedSiteMapItem);
        when(resolvedSiteMapItem.getPathInfo()).thenReturn("pathInfo");

        HstComponentConfiguration hstComponentConfiguration = mock(HstComponentConfiguration.class);
        when(resolvedSiteMapItem.getHstComponentConfiguration()).thenReturn(hstComponentConfiguration);
        when(hstComponentConfiguration.getName()).thenReturn("configName");

        ResolvedMount resolvedMount = mock(ResolvedMount.class);
        when(resolvedSiteMapItem.getResolvedMount()).thenReturn(resolvedMount);

        Mount mount = mock(Mount.class);
        when(resolvedMount.getMount()).thenReturn(mount);
        when(mount.getProperty("publishing:gtm")).thenReturn(gtmPath);

        return request;
    }

    HstRequest exceptionThrowingRequest(String configName, String pathInfo) throws RepositoryException {
        Session session = mock(Session.class);
        when(session.getNode(any())).thenThrow(new RepositoryException(""));
        return request(configName, pathInfo, session);
    }

    Session sessionWithValues(String containerId, String auth, String env) throws RepositoryException {
        Session session = mock(Session.class);
        Node gtmHandle = mock(Node.class);
        when(gtmHandle.getName()).thenReturn("handleName");
        Node gtmNode = mock(Node.class);
        when(gtmHandle.getNodes("handleName")).thenReturn(TestUtil.iterator(Collections.singleton(gtmNode)));
        when(session.nodeExists("gtmPath")).thenReturn(true);
        when(session.getNode("gtmPath")).thenReturn(gtmHandle);
        Property state = prop("published");
        Property containerIdProp = prop(containerId);
        Property authProp = prop(auth);
        Property envProp = prop(env);
        when(gtmNode.hasProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(true);
        when(gtmNode.getProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(state);
        when(gtmNode.getProperty("publishing:containerid")).thenReturn(containerIdProp);
        when(gtmNode.getProperty("publishing:auth")).thenReturn(authProp);
        when(gtmNode.getProperty("publishing:env")).thenReturn(envProp);
        return session;
    }

    Property prop(String value) throws RepositoryException {
        Property property = mock(Property.class);
        when(property.getString()).thenReturn(value);
        return property;
    }

}
