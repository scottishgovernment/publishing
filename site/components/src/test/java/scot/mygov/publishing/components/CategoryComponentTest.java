package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

/**
 * Created by z418868 on 19/09/2019.
 */
public class CategoryComponentTest {

    static final String INDEX = "index";

    @Test
    public void childrenSetAsExcepted() {

        // ARRANGE
        HippoFolderBean folder = mock(HippoFolderBean.class);
        HippoBean basebean = folder;
        HippoBean index = indexBean();
        HippoBean subfolderIndexBean = indexBean();
        HippoBean subfolder = subfolderBean("subfolder", subfolderIndexBean);
        HippoBean siteFurnitureFolder = subfolderBean("site-furniture");
        HippoBean adminfolder = subfolderBean("administration");
        HippoBean article = mock(HippoBean.class);
        when(article.getName()).thenReturn("article");

        List<HippoBean> children = new ArrayList<>();
        Collections.addAll(children, index, siteFurnitureFolder, adminfolder, subfolder, article);
        when(folder.getChildBeans(any(Class.class))).thenReturn(children);

        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        HippoBean bean = mock(HippoBean.class);
        when(bean.getParentBean()).thenReturn(folder);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(bean);
        when(context.getSiteContentBaseBean()).thenReturn(basebean);
        when(folder.getChildBeans(HippoBean.class)).thenReturn(children);

        // ACT
        CategoryComponent.setCategoryAttributes(request);

        // ARRANGE
        List<HippoBean> expected = new ArrayList<>();
        Collections.addAll(expected, subfolderIndexBean, article);
        verify(request).setAttribute("children", expected);
    }

    @Test
    public void notAttributesSetIfNoContentBean() {
        // ARRANGE
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(null);

        // ACT
        CategoryComponent.setCategoryAttributes(request);

        // ASSERT
        verify(request, never()).setAttribute(any(), any());
    }

    HippoBean indexBean() {
        HippoBean index = mock(HippoBean.class);
        when(index.getName()).thenReturn(INDEX);
        return index;
    }

    HippoBean subfolderBean(String name, HippoBean indexBean) {
        HippoBean folder = mock(HippoBean.class);
        when(folder.getName()).thenReturn(name);
        when(folder.isHippoFolderBean()).thenReturn(true);
        when(folder.getBean(INDEX)).thenReturn(indexBean, indexBean);
        return folder;
    }

    HippoBean subfolderBean(String name) {
        HippoBean indexBean = indexBean();
        return subfolderBean(name, indexBean);
    }
}
