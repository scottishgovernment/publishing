package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.junit.Assert;
import org.junit.Test;
import org.onehippo.forge.breadcrumb.om.BreadcrumbItem;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 19/09/2019.
 */
public class BreadcrumbComponentTest {

    @Test
    public void constructExpectedBreadcrumbForArticle() {
        // ARRANGE
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        HstLinkCreator linkCreator = mock(HstLinkCreator.class);
        HippoBean article = article("article");
        HippoBean categoryIndex = indexbean("categoryIndex");
        HippoBean category = folderbean("category", categoryIndex);
        HippoBean homeIndex = indexbean("homeIndex");
        HippoBean home = folderbean("home", homeIndex);

        when(article.getParentBean()).thenReturn(category);
        when(category.getParentBean()).thenReturn(home);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(article);
        when(context.getSiteContentBaseBean()).thenReturn(home);
        when(context.getHstLinkCreator()).thenReturn(linkCreator);

        // ACT
        List<BreadcrumbItem> actual = BreadcrumbComponent.constructBreadcrumb(request);

        // ASSERT
        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(actual.get(0).getTitle(), "homeIndex-title");
        Assert.assertEquals(actual.get(1).getTitle(), "categoryIndex-title");
        verify(linkCreator).create(same(homeIndex), any());
        verify(linkCreator).create(same(categoryIndex), any());
    }


    @Test
    public void constructExpectedBreadcrumbForCategory() {
        // ARRANGE
        HstRequest request = mock(HstRequest.class);
        HstRequestContext context = mock(HstRequestContext.class);
        HstLinkCreator linkCreator = mock(HstLinkCreator.class);
        HippoBean subcategoryIndex = indexbean("subcategoryIndex");
        HippoBean subcategory = folderbean("subcategory", subcategoryIndex);
        HippoBean categoryIndex = indexbean("categoryIndex");
        HippoBean category = folderbean("category", categoryIndex);
        HippoBean homeIndex = indexbean("homeIndex");
        HippoBean home = folderbean("home", homeIndex);

        when(subcategory.getParentBean()).thenReturn(category);
        when(category.getParentBean()).thenReturn(home);
        when(request.getRequestContext()).thenReturn(context);
        when(context.getContentBean()).thenReturn(subcategoryIndex);
        when(context.getSiteContentBaseBean()).thenReturn(home);
        when(context.getHstLinkCreator()).thenReturn(linkCreator);

        // ACT
        List<BreadcrumbItem> actual = BreadcrumbComponent.constructBreadcrumb(request);

        // ASSERT
        Assert.assertEquals(2, actual.size());
        Assert.assertEquals(actual.get(0).getTitle(), "homeIndex-title");
        Assert.assertEquals(actual.get(1).getTitle(), "categoryIndex-title");
        verify(linkCreator).create(same(homeIndex), any());
        verify(linkCreator).create(same(categoryIndex), any());
    }

    HippoBean article(String identifier) {
        HippoBean bean = mock(HippoBean.class);
        when(bean.getIdentifier()).thenReturn(identifier);
        when(bean.getName()).thenReturn(identifier);
        withTitle(bean, identifier);
        return bean;
    }

    HippoBean indexbean(String identifier) {
        HippoBean bean = mock(HippoBean.class);
        when(bean.getIdentifier()).thenReturn(identifier);
        when(bean.getName()).thenReturn("index");
        withTitle(bean, identifier);
        return bean;
    }


    void withTitle(HippoBean bean, String id) {
        when(bean.getSingleProperty("publishing:title")).thenReturn(title(id));
    }

    HippoBean folderbean(String identifier, HippoBean indexBean) {
        HippoBean bean = mock(HippoBean.class);
        when(bean.isHippoFolderBean()).thenReturn(true);
        when(bean.getIdentifier()).thenReturn(identifier);
        when(bean.getName()).thenReturn(identifier);
        withTitle(bean, identifier);
        when(bean.getBean("index")).thenReturn(indexBean);
        when(indexBean.getParentBean()).thenReturn(bean);
        return bean;
    }

    String title(String id) {
        return id + "-title";
    }

}
