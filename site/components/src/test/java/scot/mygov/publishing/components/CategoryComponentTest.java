package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 19/09/2019.
 */
public class CategoryComponentTest {

    static final String INDEX = "index";

    @Test
    public void getChildrenReturnsExpectedResutls() {

        // ARRANGE

        HippoFolderBean folder = mock(HippoFolderBean.class);
        HippoBean baseBean = folder;
        List<HippoBean> children = new ArrayList<>();
        HippoBean index = indexBean();
        HippoBean subfolder = subfolderBean("subfolder");
        HippoBean footerfolder = subfolderBean("footer");
        HippoBean article = mock(HippoBean.class);
        when(article.getName()).thenReturn("article");
        Collections.addAll(children, index, footerfolder, subfolder, article);
        when(folder.getChildBeans(any(Class.class))).thenReturn(children);

        // ACT
        List<HippoBean> actual = CategoryComponent.getChildren(folder, baseBean);

        // ARRANGE
        assertEquals(actual.size(), 2);
        assertSame(actual.get(0).getName(), INDEX);
        assertSame(actual.get(1), article);
    }


    HippoBean indexBean() {
        HippoBean index = mock(HippoBean.class);
        when(index.getName()).thenReturn(INDEX);
        return index;
    }

    HippoBean subfolderBean(String name) {
        HippoBean folder = mock(HippoBean.class);
        when(folder.getName()).thenReturn(name);
        when(folder.isHippoFolderBean()).thenReturn(true);
        HippoBean index = indexBean();
        when(folder.getBean(INDEX)).thenReturn(index, index);
        return folder;
    }
}
