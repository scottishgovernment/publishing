package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.junit.Test;
import org.mockito.Mockito;
import scot.mygov.publishing.beans.Base;
import scot.mygov.publishing.beans.Mirror;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

/**
 * Created by z418868 on 19/09/2019.
 */
public class CategoryComponentTest {

    static final String INDEX = "index";

    @Test
    public void getChildrenIncludesAdministrationFolderIfNotInRoot() {

        // ARRANGE - note that the folder are base bean are different
        HippoFolderBean folder = mock(HippoFolderBean.class);
        HippoBean siteFurnitureFolder = subfolderBean("site-furniture");
        HippoBean adminFolder = subfolderBean("administration");
        HippoBean index = indexBean();

        List<HippoBean> children = new ArrayList<>();
        Collections.addAll(children, siteFurnitureFolder, adminFolder, index);
        when(folder.getChildBeans(any(Class.class))).thenReturn(children);

        // ACT
        List<CategoryComponent.Wrapper> actual = CategoryComponent.getChildren(folder);

        // ASSERT -- this isnt the root so subfolders should not have nee excluded
        assertTrue("should be 2 children", actual.size() == 2);
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

    @Test
    public void childAttributeSetIfContentBeanPresent() {
        // ARRANGE
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        Base contentBean = mock(Base.class);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(contentBean);
        HippoBean folder = mock(HippoFolderBean.class);
        when(contentBean.getParentBean()).thenReturn(folder);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(contentBean);

        HippoBean base = mock(HippoFolderBean.class);
        when(context.getSiteContentBaseBean()).thenReturn(base);

        request.getRequestContext().getSiteContentBaseBean();
        // ACT
        CategoryComponent.setCategoryAttributes(request);

        // ASSERT
        verify(request).setAttribute(any(), any());
    }

    HippoBean indexBean() {
        HippoBean index = mock(HippoBean.class);
        when(index.getName()).thenReturn(INDEX);
        when(index.getSingleProperty(eq("publishing:showInParent"), eq(true))).thenReturn(true);
        return index;
    }

    HippoBean subfolderBean(String name, HippoBean indexBean) {
        HippoBean folder = mock(HippoBean.class);
        when(folder.getName()).thenReturn(name);
        when(folder.isHippoFolderBean()).thenReturn(true);
        when(folder.getBean(INDEX)).thenReturn(indexBean, indexBean);
        when(folder.getSingleProperty(eq("publishing:showInParent"), eq(true))).thenReturn(true);
        return folder;
    }

    HippoBean subfolderBean(String name) {
        HippoBean indexBean = indexBean();
        return subfolderBean(name, indexBean);
    }


    Mirror mirror(Base mirrored) {
        Mirror mirror = mock(Mirror.class);
        when(mirror.getDocument()).thenReturn(mirrored);
        when(mirror.getSingleProperty(eq("publishing:includeInParent"), eq(true))).thenReturn(true);
        return mirror;
    }

}
