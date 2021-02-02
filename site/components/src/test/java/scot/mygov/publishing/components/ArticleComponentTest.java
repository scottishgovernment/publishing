package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by z418868 on 19/09/2019.
 */
public class ArticleComponentTest {

    @Test
    public void prevNullIfFirstChild() {
        // ARRANGE
        List<CategoryComponent.Wrapper> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        when(self.isSelf(same(self))).thenReturn(true);
        when(self.isSelf(same(another))).thenReturn(false);
        Collections.addAll(children, wrap(self), wrap(another));
        HstRequest request = request(children, self);
        HstResponse response = mock(HstResponse.class);

        // ACT
        ArticleComponent.setArticleAttributes(request, response);

        // ASSERT
        verify(request).setAttribute("prev", null);
    }

    @Test
    public void prevIsPreviousSiblingIfNotFirst() {
        // ARRANGE
        List<CategoryComponent.Wrapper> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        when(self.isSelf(same(self))).thenReturn(true);
        when(self.isSelf(same(another))).thenReturn(false);
        Collections.addAll(children, wrap(another), wrap(self));
        HstRequest request = request(children, self);
        HstResponse response = mock(HstResponse.class);

        // ACT
        ArticleComponent.setArticleAttributes(request, response);

        // ASSERT
        verify(request).setAttribute("prev", another);
    }

    @Test
    public void nextReturnsNullIfLastChild() {

        // ARRANGE
        List<CategoryComponent.Wrapper> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        when(self.isSelf(same(self))).thenReturn(true);
        when(self.isSelf(same(another))).thenReturn(false);
        Collections.addAll(children, wrap(self), wrap(another));
        HstRequest request = request(children, self);
        HstResponse response = mock(HstResponse.class);

        // ACT
        ArticleComponent.setArticleAttributes(request, response);

        // ASSERT
        verify(request).setAttribute("prev", null);
    }

    @Test
    public void nextReturnsNextSiblingIfNotLast() {
        // ARRANGE
        List<CategoryComponent.Wrapper> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        when(self.isSelf(same(self))).thenReturn(true);
        when(self.isSelf(same(another))).thenReturn(false);
        Collections.addAll(children, wrap(self), wrap(another));
        Collections.addAll(children, wrap(self), wrap(another));
        HstRequest request = request(children, self);
        HstResponse response = mock(HstResponse.class);

        // ACT
        ArticleComponent.setArticleAttributes(request, response);

        // ASSERT
        verify(request).setAttribute("next", another);

    }

    CategoryComponent.Wrapper wrap(HippoBean bean) {
        return new CategoryComponent.Wrapper(bean);
    }

    HstRequest request(List<CategoryComponent.Wrapper> children, HippoBean bean) {
        HippoBean folder = mock(HippoBean.class);
        HippoBean index = mock(HippoBean.class);
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        HstLinkCreator linkCreator = mock(HstLinkCreator.class);
        HstLink link = mock(HstLink.class);
        when(link.getPath()).thenReturn("link");
        when(request.getPathInfo()).thenReturn("/link");
        when(linkCreator.create(any(HippoBean.class), any(HstRequestContext.class))).thenReturn(link);
        when(request.getAttribute("children")).thenReturn(children);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(bean);
        when(folder.getBean("index")).thenReturn(index);
        when(bean.getParentBean()).thenReturn(folder);
        when(context.getHstLinkCreator()).thenReturn(linkCreator);
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
        HstResponse response = mock(HstResponse.class);

        // ACT
        ArticleComponent.setArticleAttributes(request, response);

        // ASSERT
        verify(request, never()).setAttribute(any(), any());
    }

}
