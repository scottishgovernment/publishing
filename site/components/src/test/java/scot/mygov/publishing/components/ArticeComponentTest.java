package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Created by z418868 on 19/09/2019.
 */
public class ArticeComponentTest {

    @Test
    public void prevReturnsNullIfFirstChild() {
        // ARRANGE
        List<HippoBean> children = new ArrayList<>();
        HippoBean self = Mockito.mock(HippoBean.class);
        HippoBean another = Mockito.mock(HippoBean.class);
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
        HippoBean self = Mockito.mock(HippoBean.class);
        HippoBean another = Mockito.mock(HippoBean.class);
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
        HippoBean self = Mockito.mock(HippoBean.class);
        HippoBean another = Mockito.mock(HippoBean.class);
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
        HippoBean self = Mockito.mock(HippoBean.class);
        HippoBean another = Mockito.mock(HippoBean.class);
        Collections.addAll(children, self, another);

        // ACT
        HippoBean actual = ArticleComponent.next(children, 0);

        // ASSERT
        assertSame(actual, another);
    }
}
