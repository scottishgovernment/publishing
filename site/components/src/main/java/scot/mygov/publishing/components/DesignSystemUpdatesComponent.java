package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.manager.ObjectConverter;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.util.ObjectConverterUtils;
import org.hippoecm.repository.util.DateTools;
import org.onehippo.cms7.essentials.components.CommonComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.beans.UpdateHistory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@ParametersInfo(type = DesignSystemUpdatesComponentInfo.class)
public class DesignSystemUpdatesComponent extends CommonComponent {

    private static final Logger LOG = LoggerFactory.getLogger(DesignSystemUpdatesComponent.class);

    private static final String XPATH_TEMPLATE =
            "/jcr:root/content/documents/designsystem//element(*,publishing:UpdateHistory)" +
            "[@%s >= %s]" +
            "order by @publishing:lastUpdated descending";

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

    List<UpdateHistory> doGetUpdates(HstRequest request, int limit) throws RepositoryException, ObjectBeanManagerException {
        NodeIterator it = query(request, 50).execute().getNodes();
        List<UpdateHistory> results = new ArrayList<>();
        while (it.hasNext()) {
            Node node = it.nextNode();
            UpdateHistory history = (UpdateHistory) objectConverter.getObject(node);
            if (isNotBlank(history.getUpdateTextLong().getContent())) {
                results.add(history);
            }
        }

        if (results.size() > limit) {
            return results.subList(0, limit);
        } else {
            return results;
        }
    }

    Query query(HstRequest request, int limit) throws RepositoryException {
        HstRequestContext context = request.getRequestContext();
        Calendar cutoff = threeMonthsAgo();
        String xpathProperty = DateTools.getPropertyForResolution("publishing:lastUpdated", DateTools.Resolution.DAY);
        String xpathDate = DateTools.createXPathConstraint(request.getRequestContext().getSession(), cutoff, DateTools.Resolution.DAY);
        String xpath = String.format(XPATH_TEMPLATE, xpathProperty, xpathDate);
        QueryManager queryManager = context.getSession().getWorkspace().getQueryManager();
        Query query = queryManager.createQuery(xpath, Query.XPATH);
        query.setLimit(limit);
        return query;
    }

    Calendar threeMonthsAgo() {
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        Date d = Date.from(threeMonthsAgo.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal;
    }
}