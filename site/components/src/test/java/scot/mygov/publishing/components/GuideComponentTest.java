package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.junit.Test;
import org.mockito.Mockito;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Created by z418868 on 25/06/2020.
 */
public class GuideComponentTest {

    @Test
    public void getFirstGuidePageReturnsNullForNoChildren() {
        HstRequest request = mock(HstRequest.class);
        Mockito.when(request.getAttribute("children")).thenReturn(emptyList());
        HippoBean actual = GuideComponent.getFirstGuidePage(request);
        assertNull(actual);
    }

    @Test
    public void getFirstGuidePageReturnsFirstChild() {
        HstRequest request = mock(HstRequest.class);
        HippoBean bean = mock(HippoBean.class);
        CategoryComponent.Wrapper child = mock(CategoryComponent.Wrapper.class);
        Mockito.when(child.getBean()).thenReturn(bean);
        Mockito.when(request.getAttribute("children")).thenReturn(singletonList(child));
        HippoBean actual = GuideComponent.getFirstGuidePage(request);
        assertSame(bean, actual);
    }
}
