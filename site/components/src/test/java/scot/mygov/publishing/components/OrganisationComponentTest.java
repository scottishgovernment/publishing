package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.HstQueryBuilder;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.junit.Test;
import scot.mygov.publishing.HippoUtils;
import scot.mygov.publishing.beans.Base;
import scot.mygov.publishing.beans.Organisation;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class OrganisationComponentTest {

    @Test
    public void setServicesUsersQueryResults() throws QueryException {

        HstRequest request = mock(HstRequest.class);
        HstRequestContext requestContext = mock(HstRequestContext.class);
        Organisation organisation = mock(Organisation.class);
        when(request.getRequestContext()).thenReturn(requestContext);
        when(requestContext.getContentBean()).thenReturn(organisation);
        OrganisationComponent.hippoUtils = mock(HippoUtils.class);
        HstQuery query = mock(HstQuery.class);

        HstQueryResult result = mock(HstQueryResult.class);
        HippoBeanIterator beans = mock(HippoBeanIterator.class);
        when(result.getHippoBeans()).thenReturn(beans);
        when(query.execute()).thenReturn(result);
        HstQueryBuilder builder = queryBuilder(query);
        when(OrganisationComponent.hippoUtils.createQuery(any())).thenReturn(builder);

        // ACT
        OrganisationComponent.setServices(request);

        // ASSERT
        verify(request).setAttribute(eq("services"), same(beans));
    }

    @Test
    public void setServicesUsersEmptyListIfExceptionThrown() throws QueryException {

        HstRequest request = mock(HstRequest.class);
        HstRequestContext requestContext = mock(HstRequestContext.class);
        Organisation organisation = mock(Organisation.class);
        when(request.getRequestContext()).thenReturn(requestContext);
        when(requestContext.getContentBean()).thenReturn(organisation);
        OrganisationComponent.hippoUtils = mock(HippoUtils.class);
        HstQuery query = mock(HstQuery.class);
        when(query.execute()).thenThrow(new QueryException(""));
        HstQueryBuilder builder = queryBuilder(query);
        when(OrganisationComponent.hippoUtils.createQuery(any())).thenReturn(builder);

        // ACT
        OrganisationComponent.setServices(request);

        // ASSERT
        verify(request).setAttribute(eq("services"), eq(emptyList()));
    }

    HstQueryBuilder queryBuilder(HstQuery query) {
        HstQueryBuilder builder = mock(HstQueryBuilder.class);
        when(builder.ofTypes(Base.class)).thenReturn(builder);
        when(builder.orderByAscending("publishing:title")).thenReturn(builder);
        when(builder.where(any())).thenReturn(builder);
        when(builder.build()).thenReturn(query);
        return builder;
    }
}
