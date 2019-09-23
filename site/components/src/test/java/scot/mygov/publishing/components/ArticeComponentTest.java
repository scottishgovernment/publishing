package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 19/09/2019.
 */
public class ArticeComponentTest {

    @Test
    public void prevReturnsNullIfFirstChild() {
        // ARRANGE
        List<HippoBean> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        Collections.addAll(children, self, another);

        // ACT
        HippoBean actual = ArticleComponent.prev(children, 0);

        // ASSERT
        assertNull(actual);
    }

    @Test
    public void prevReturnsPreviousSiblingIfNotFirst() {
        // ARRANGE
        List<HippoBean> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        Collections.addAll(children, another, self);

        // ACT
        HippoBean actual = ArticleComponent.prev(children, 1);

        // ASSERT
        assertSame(actual, another);
    }

    @Test
    public void nextReturnsNullIfLastChild() {
        // ARRANGE
        List<HippoBean> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        Collections.addAll(children, self, another);

        // ACT
        HippoBean actual = ArticleComponent.next(children, 1);

        // ASSERT
        assertNull(actual);
    }

    @Test
    public void nextReturnsNextSiblingIfNotLast() {
        // ARRANGE
        List<HippoBean> children = new ArrayList<>();
        HippoBean self = mock(HippoBean.class);
        HippoBean another = mock(HippoBean.class);
        Collections.addAll(children, self, another);

        // ACT
        HippoBean actual = ArticleComponent.next(children, 0);

        // ASSERT
        assertSame(actual, another);
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
    public void sequenceableFlagSetToMathcIndexFile() {

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
}
