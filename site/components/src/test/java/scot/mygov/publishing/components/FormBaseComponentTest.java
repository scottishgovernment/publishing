package scot.mygov.publishing.components;

import org.hippoecm.hst.configuration.components.HstComponentConfiguration;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedMount;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.hippoecm.repository.HippoStdNodeType;
import org.junit.Test;
import scot.mygov.publishing.TestUtil;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 01/09/2022.
 */
public class FormBaseComponentTest {

    @Test
    public void setsExpectedValuesFromRecaptchaPath() throws RepositoryException {

        // ARRANGE
        HstRequest request = request("", "", true, "expectedSitekey");

        // ACT
        FormBaseComponent.setRecaptchaAttributes(request);

        // ASSERT
        verify(request).setAttribute("recaptchaEnabled", true);
        verify(request).setAttribute("recaptchaSitekey", "expectedSitekey");

    }

    @Test
    public void repExceptionResultsInDefaultValues() throws RepositoryException {

        // ARRANGE
        HstRequest request = exceptionThrowingRequest("", "");

        // ACT
        FormBaseComponent.setRecaptchaAttributes(request);

        // ASSERT
        verify(request).setAttribute("recaptchaEnabled", false);
        verify(request).setAttribute("recaptchaSitekey", "");
    }

    @Test
    public void noGtmPathOnMountResultsInDefaultValues() {

        // ARRANGE
        HstRequest request = requestWithGtmPath(null);

        // ACT
        FormBaseComponent.setRecaptchaAttributes(request);

        // ASSERT
        verify(request).setAttribute("recaptchaEnabled", false);
        verify(request).setAttribute("recaptchaSitekey", "");
    }

    @Test
    public void gtmPathPointingAtNonExistantPathResultsInDefaultValues() throws RepositoryException {

        // ARRANGE
        Session session = mock(Session.class);
        HstRequest request = request("", "", session);

        // ACT
        FormBaseComponent.setRecaptchaAttributes(request);

        // ASSERT
        verify(request).setAttribute("recaptchaEnabled", false);
        verify(request).setAttribute("recaptchaSitekey", "");
    }

    HstRequest request(String configName, String pathInfo, boolean enabled, String sitekey) throws RepositoryException {
        Session session = sessionWithValues(enabled, sitekey);
        HstRequestContext context = context(null);
        return request(configName, pathInfo, session, context);
    }

    HstRequestContext context(HippoBean bean) {
        HstRequestContext context = mock(HstRequestContext.class);
        when(context.getContentBean()).thenReturn(bean);
        return context;
    }

    HstRequest request(String configName, String pathInfo, Session session) throws RepositoryException {
        HstRequestContext context = context(null);
        return request(configName, pathInfo, session, context);
    }

    HstRequest request(String configName, String pathInfo, Session session, HstRequestContext context) throws RepositoryException {
        HstRequest request = mock(HstRequest.class);

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
        when(mount.getProperty("publishing:recaptcha")).thenReturn("recaptchaPath");

        return request;
    }

    HstRequest requestWithGtmPath(String gtmPath) {
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
        when(mount.getProperty("publishing:recaptcha")).thenReturn(gtmPath);

        return request;
    }

    HstRequest exceptionThrowingRequest(String configName, String pathInfo) throws RepositoryException {
        Session session = mock(Session.class);
        when(session.nodeExists(any())).thenThrow(new RepositoryException(""));
        when(session.getNode(any())).thenThrow(new RepositoryException(""));
        return request(configName, pathInfo, session);
    }

    Session sessionWithValues(boolean enabled, String sitekey) throws RepositoryException {
        Session session = mock(Session.class);
        Node recaptchaHandle = mock(Node.class);
        when(recaptchaHandle.getName()).thenReturn("handleName");
        Node recaptchaNode = mock(Node.class);
        when(recaptchaHandle.getNodes("handleName")).thenReturn(TestUtil.iterator(Collections.singleton(recaptchaNode)));
        when(session.nodeExists("recaptchaPath")).thenReturn(true);
        when(session.getNode("recaptchaPath")).thenReturn(recaptchaHandle);
        Property state = prop("published");
        Property sitekeyProp = prop(sitekey);
        Property enabledProp = prop(enabled);

        when(recaptchaNode.hasProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(true);
        when(recaptchaNode.getProperty(HippoStdNodeType.HIPPOSTD_STATE)).thenReturn(state);
        when(recaptchaNode.getProperty("publishing:sitekey")).thenReturn(sitekeyProp);
        when(recaptchaNode.getProperty("publishing:enabled")).thenReturn(enabledProp);

        return session;
    }

    Property prop(String value) throws RepositoryException {
        Property property = mock(Property.class);
        when(property.getString()).thenReturn(value);
        return property;
    }

    Property prop(boolean value) throws RepositoryException {
        Property property = mock(Property.class);
        when(property.getBoolean()).thenReturn(value);
        return property;
    }
}
