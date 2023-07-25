package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.manager.ObjectConverter;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ObjectConverterUtils;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.beans.UpdateHistory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.util.Collections.singletonList;

@ParametersInfo(type = DesignSystemUpdatesComponentInfo.class)
public class DesignSystemUpdatesComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(DesignSystemUpdatesComponent.class);

    private static final String XPATH_TEMPLATE =
            "/jcr:root/content/documents/designsystem//element(*,publishing:dsarticle)" +
            "/element(*,publishing:UpdateHistory)" +
            "[@publishing:lastUpdated >= xs:dateTime('%s')]" +
            "/publishing:updateTextLong[jcr:contains(@hippostd:content, '*')]/.. " +
            "order by @publishing:lastUpdated descending";

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private final ObjectConverter objectConverter = ObjectConverterUtils.createObjectConverter(singletonList(UpdateHistory.class));

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        DesignSystemUpdatesComponentInfo paramInfo = getComponentParametersInfo(request);
        request.setAttribute("updates", getUpdates(request, paramInfo.getLimit()));
    }

    List<UpdateHistory> getUpdates(HstRequest request, int limit) {
        try {
            return doGetUpdates(request, limit);
        } catch (RepositoryException e) {
            LOG.error("Query exception getting design system updates", e);
            return Collections.emptyList();
        } catch (ObjectBeanManagerException e) {
            LOG.error("Bean mapping exception getting design system updates", e);
            return Collections.emptyList();
        }
    }

    List<UpdateHistory> doGetUpdates(HstRequest request, int limit) throws RepositoryException, ObjectBeanManagerException{
        NodeIterator it = query(request, limit).execute().getNodes();
        List<UpdateHistory> results = new ArrayList<>();
        while (it.hasNext()) {
            Node node = it.nextNode();
            results.add((UpdateHistory) objectConverter.getObject(node));
        }
        Collections.sort(results, Comparator.comparing(UpdateHistory::getLastUpdated).reversed());
        return results;
    }

    Query query(HstRequest request, int limit) throws RepositoryException {
        HstRequestContext context = request.getRequestContext();
        String xpath = String.format(XPATH_TEMPLATE, threeMonthsAgoString());
        LOG.info("XPATH: {}", xpath);
        QueryManager queryManager = context.getSession().getWorkspace().getQueryManager();
        Query query = queryManager.createQuery(xpath, Query.XPATH);
        query.setLimit(limit);
        return query;
    }

    String threeMonthsAgoString() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now().minusMonths(3).withHour(0).withMinute(0).withSecond(0);
        return DATE_FORMAT.format(offsetDateTime);
    }
}