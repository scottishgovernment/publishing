package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.builder.Constraint;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;
import scot.mygov.publishing.beans.Base;
import scot.mygov.publishing.beans.Organisation;

import static java.util.Collections.emptyList;
import static org.hippoecm.hst.content.beans.query.builder.ConstraintBuilder.constraint;

public class OrganisationComponent extends EssentialsContentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(OrganisationComponent.class);

    static HippoUtils hippoUtils = new HippoUtils();

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        setServices(request);
    }

    static void setServices(HstRequest request) {

        HippoBean siteBean = request.getRequestContext().getSiteContentBaseBean();
        Organisation org = (Organisation) request.getRequestContext().getContentBean();
        String serviceProvider = org.getServiceprovider();
        try {
            HstQuery query = hippoUtils.createQuery(siteBean)
                    .ofTypes(Base.class)
                    .orderByAscending("publishing:title")
                    .where(serviceProviderMatches(serviceProvider))
                    .build();
            HstQueryResult result = query.execute();
            request.setAttribute("services", result.getHippoBeans());
        } catch (QueryException e) {
            LOG.warn("Unable to get services for org {}", org.getPath(), e);
            request.setAttribute("services", emptyList());
        }
    }

    static Constraint serviceProviderMatches(String serviceProvider) {
        return constraint("publishing:serviceproviders").equalTo(serviceProvider);
    }
}
