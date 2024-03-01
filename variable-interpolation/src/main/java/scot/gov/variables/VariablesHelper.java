package scot.gov.variables;

import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hippoecm.hst.resourcebundle.CompositeResourceBundle;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.RowIterator;
import java.util.*;

public class VariablesHelper {

    private static final Logger LOG = LoggerFactory.getLogger(VariablesHelper.class);

    public static final String VARIABLES_PATH = "/content/documents/var";

    public static final String VARIABLE_QUERY = "/jcr:root" + VARIABLES_PATH
            + "//element(*, resourcebundle:resourcebundle)/@resourcebundle:id";

    private VariablesHelper() {
        // hide constructor
    }

    public static ResourceBundle getVariablesResourceBundle(Session session) {
        HstRequestContext hstRequestContext = RequestContextProvider.get();

        if (hstRequestContext == null) {
            LOG.info("hstRequestContext, returned empty resource bundle ");
            return new EmptyResourceBundle();
        }

        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(VARIABLE_QUERY, Query.XPATH);
            RowIterator entries = query.execute().getRows();
            if (entries.getSize() == 0) {
                LOG.info("no variables found, returning empty resource bundle ");
                return new EmptyResourceBundle();
            }
            List<ResourceBundle> allVariableResourceBundles = new ArrayList<>();
            while (entries.hasNext()) {
                String bundleName = entries.nextRow().getValue("resourcebundle:id").getString();
                //using directly the HST ResourceBundleUtil to reduce overhead
                ResourceBundle bundle = org.hippoecm.hst.resourcebundle.ResourceBundleUtils.getBundle(bundleName, null);
                allVariableResourceBundles.add(bundle);
            }

            // in case of resource bundles containing entries with identical keys or resource bundle with the
            // same id, the last published one will always be preferred. This is caused by the order by parameter
            // placed in the jcr query statement
            ResourceBundle[] resourceBundles = allVariableResourceBundles.stream().toArray(ResourceBundle[]::new);
            return new CompositeResourceBundle(resourceBundles);
        } catch (RepositoryException e) {
            LOG.error("Failed to query for variables resource bundle documents.", e);
            return new EmptyResourceBundle();
        }
    }

    static class EmptyResourceBundle extends ResourceBundle {
        @Override
        public Enumeration<String> getKeys() {
            return Collections.emptyEnumeration();
        }

        @Override
        protected Object handleGetObject(final String key) {
            return null;
        }
    }
}
