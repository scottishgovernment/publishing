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
    public void getChildrenExcludesExpectedItemsFromRoot() {

        // ARRANGE
        HippoFolderBean folder = mock(HippoFolderBean.class);
        HippoBean basebean = folder;
        HippoBean siteFurnitureFolder = subfolderBean("site-furniture");
        HippoBean adminFolder = subfolderBean("administration");
        HippoBean index = indexBean();

        List<HippoBean> children = new ArrayList<>();
        Collections.addAll(children, siteFurnitureFolder, adminFolder, index);
        when(folder.getChildBeans(any(Class.class))).thenReturn(children);

        // ACT
        List<CategoryComponent.Wrapper> actual = CategoryComponent.getChildren(folder, basebean);

        // ASSERT -- these items should all have been excluded
        assertEquals(emptyList(), actual);
    }

    @Test
    public void getChildrenIncludesAdministrationFolderIfNotInRoot() {

        // ARRANGE - note that the folder are base bean are different
        HippoFolderBean folder = mock(HippoFolderBean.class);
        HippoBean basebean = mock(HippoFolderBean.class);
        HippoBean siteFurnitureFolder = subfolderBean("site-furniture");
        HippoBean adminFolder = subfolderBean("administration");
        HippoBean index = indexBean();

        List<HippoBean> children = new ArrayList<>();
        Collections.addAll(children, siteFurnitureFolder, adminFolder, index);
        when(folder.getChildBeans(any(Class.class))).thenReturn(children);

        // ACT
        List<CategoryComponent.Wrapper> actual = CategoryComponent.getChildren(folder, basebean);

        // ASSERT -- this isnt the root so subfolders should not have nee excluded
        assertTrue("should be 2 children", actual.size() == 2);
    }

    @Test
    public void getChildrenPutsPinnedItemsFirst() {
        // ARRANGE
        HippoFolderBean folder = mock(HippoFolderBean.class);
        HippoBean basebean = mock(HippoFolderBean.class);

        Base unpinned = article(false);
        Base pinned = article(true);

        List<HippoBean> children = new ArrayList<>();
        Collections.addAll(children, unpinned, pinned);
        when(folder.getChildBeans(any(Class.class))).thenReturn(children);

        // ACT
        List<CategoryComponent.Wrapper> actual = CategoryComponent.getChildren(folder, basebean);

        // ASSERT -- these items should all have been excluded
        assertSame(actual.get(0).getBean(), pinned);
        assertEquals(actual.get(0).getPinned(), true);
        assertSame(actual.get(1).getBean(), unpinned);
        assertEquals(actual.get(1).getPinned(), false);
    }

    @Test
    public void getChildrenSetsPinnedForMirrors() {
        // ARRANGE
        HippoFolderBean folder = mock(HippoFolderBean.class);
        HippoBean basebean = mock(HippoFolderBean.class);

        Base unpinned = article(false);
        Base pinned = article(true);
        Mirror unpinnedMirror = mirror(pinned, false);
        Mirror pinnedMirror = mirror(unpinned, true);

        List<HippoBean> children = new ArrayList<>();
        Collections.addAll(children, pinnedMirror, unpinnedMirror);
        when(folder.getChildBeans(any(Class.class))).thenReturn(children);

        // here we are testing a couple of things:
        // - the pinned status of the thing being mirrored is not relevant
        // - the order is correct according to the mirrored pin status

        // ACT
        List<CategoryComponent.Wrapper> actual = CategoryComponent.getChildren(folder, basebean);

        // ASSERT -- these items should all have been excluded
        assertSame(actual.get(0).getBean(), unpinned);
        assertEquals(actual.get(0).getPinned(), true);
        assertSame(actual.get(1).getBean(), pinned);
        assertEquals(actual.get(1).getPinned(), false);
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
//    static void setCategoryAttributes(HstRequest request) {
//
//        if (!hasContentBean(request)) {
//            return;
//        }
//
//        HippoBean bean = getDocumentBean(request);
//        HippoBean baseBean = request.getRequestContext().getSiteContentBaseBean();
//        HippoFolderBean folder = (HippoFolderBean) bean.getParentBean();
//        request.setAttribute("children", getChildren(folder, baseBean));
//    }
//static HippoBean getDocumentBean(HstRequest request) {
//    HippoBean document = request.getRequestContext().getContentBean();
//    return document instanceof Mirror ? mirroredDocument(document) : document;
//}

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

        // ACT
        CategoryComponent.setCategoryAttributes(request);

        // ASSERT
        verify(request).setAttribute(any(), any());
    }

    @Test
    public void childAttributeSetIfContentBeanPresentAndIsAMirror() {
        // ARRANGE
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        Mirror contentBeanMirror = mock(Mirror.class);
        Base contentBean = mock(Base.class);
        HippoBean folder = mock(HippoFolderBean.class);
        when(contentBean.getParentBean()).thenReturn(folder);
        when(contentBeanMirror.getDocument()).thenReturn(contentBean);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(contentBeanMirror);

        // ACT
        CategoryComponent.setCategoryAttributes(request);

        // ASSERT
        verify(request).setAttribute(any(), any());
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

    Base article(boolean pinned) {
        Base base = mock(Base.class);
        when(base.getPinned()).thenReturn(pinned);
        return base;
    }

    Mirror mirror(Base mirrored, boolean pinned) {
        Mirror mirror = mock(Mirror.class);
        when(mirror.getPinned()).thenReturn(pinned);
        when(mirror.getDocument()).thenReturn(mirrored);
        return mirror;
    }

}
