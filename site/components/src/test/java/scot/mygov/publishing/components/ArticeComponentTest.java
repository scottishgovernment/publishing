package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by z418868 on 19/09/2019.
 */
public class ArticeComponentTest {

    @Test
    public void prevNullIfFirstChild() {
        // ARRANGE
        List<HippoBean> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        Collections.addAll(children, self, another);
        HstRequest request = request(children, self);

        // ACT
        ArticleComponent.setArticleAttributes(request);

        // ASSERT
        verify(request).setAttribute("prev", null);
    }

    @Test
    public void prevIsPreviousSiblingIfNotFirst() {
        // ARRANGE
        List<HippoBean> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        Collections.addAll(children, another, self);
        HstRequest request = request(children, self);

        // ACT
        ArticleComponent.setArticleAttributes(request);

        // ASSERT
        verify(request).setAttribute("prev", another);
    }

    @Test
    public void nextReturnsNullIfLastChild() {

        // ARRANGE
        List<HippoBean> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        Collections.addAll(children, self, another);
        HstRequest request = request(children, self);

        // ACT
        ArticleComponent.setArticleAttributes(request);

        // ASSERT
        verify(request).setAttribute("prev", null);
    }

    @Test
    public void nextReturnsNextSiblingIfNotLast() {
        // ARRANGE
        List<HippoBean> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        Collections.addAll(children, self, another);
        HstRequest request = request(children, self);

        // ACT
        ArticleComponent.setArticleAttributes(request);

        // ASSERT
        verify(request).setAttribute("next", another);

    }

    HstRequest request(List<HippoBean> children, HippoBean bean) {
        HippoBean folder = mock(HippoBean.class);
        HippoBean index = mock(HippoBean.class);
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        when(request.getAttribute("children")).thenReturn(children);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(bean);
        when(folder.getBean("index")).thenReturn(index);
        when(bean.getParentBean()).thenReturn(folder);
        return request;
    }

    @Test
    public void sequenceableFlagSetToFalseIfNoIndexFile() {
        // ARRANGE
        HstRequest request = mock(HstRequest.class);
        HippoBean document = mock(HippoBean.class);
        HippoBean folder = mock(HippoBean.class);
        when(document.getParentBean()).thenReturn(folder);

        // ACT
        ArticleComponent.setSequenceable(request, document);

        // ASSERT
        verify(request).setAttribute("sequenceable", false);
    }

    @Test
    public void sequenceableFlagSetToMatchIndexFile() {

        // ARRANGE
        HstRequest request = mock(HstRequest.class);
        HippoBean document = mock(HippoBean.class);
        HippoBean folder = mock(HippoBean.class);
        HippoBean index = mock(HippoBean.class);
        when(document.getParentBean()).thenReturn(folder);
        when(folder.getBean("index")).thenReturn(index);
        when(index.getSingleProperty("publishing:sequenceable")).thenReturn(Boolean.TRUE);

        // ACT
        ArticleComponent.setSequenceable(request, document);

        // ASSERT
        verify(request).setAttribute("sequenceable", true);
    }

    @Test
    public void notAttributesSetIfNoContentBean() {
        // ARRANGE
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(null);

        // ACT
        ArticleComponent.setArticleAttributes(request);

        // ASSERT
        verify(request, never()).setAttribute(any(), any());
    }

}
