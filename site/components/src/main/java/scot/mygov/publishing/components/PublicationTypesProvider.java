package scot.mygov.publishing.components;

import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.hippo.funnelback.component.MapProvider;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class PublicationTypesProvider implements MapProvider {

    private static final Logger LOG = LoggerFactory.getLogger(PublicationTypesProvider.class);

    private static final String PUBLICATION_TYPES = "publicationtypes";

    private static final String PUBLICATION_VALUE_LIST = "/content/documents/publishing/valuelists/publicationtypes/publicationtypes";

    private boolean includeNews;

    public PublicationTypesProvider(boolean includeNews) {
        this.includeNews = includeNews;
    }

    public PublicationTypesProvider() {
        this(true);
    }

    @Override
    public Map<String, String> get(HstRequestContext context) {

        try {
            Node publicationValueList = publicationTypesValueList(context);
            if (publicationValueList == null) {
                return Collections.emptyMap();
            }

            Node typesForSiteNode = publicationTypesForSiteNode(context);
            if (typesForSiteNode == null) {
                return Collections.emptyMap();
            }

            NodeIterator it = publicationValueList.getNodes("selection:listitem");
            Map<String, String> map = new TreeMap<>();
            while (it.hasNext()) {
                Node node = it.nextNode();
                String key = node.getProperty("selection:key").getString();
                if (typesForSiteNode.hasProperty(key)) {
                    map.put(key, node.getProperty("selection:label").getString());
                }
            }
            if (includeNews) {
                map.put("news", "News");
            }
            return map;
        } catch (RepositoryException e) {
            LOG.error("Failed to load publication types", e);
            return Collections.emptyMap();
        }
    }

    Node publicationTypesValueList(HstRequestContext context) throws RepositoryException {
        Node site = context.getSiteContentBaseBean().getNode();
        Session session = context.getSession();
        Node adminFolder = site.getNode("administration");
        if (adminFolder.hasNode(PUBLICATION_TYPES)) {
            return adminFolder.getNode(PUBLICATION_TYPES).getNode(PUBLICATION_TYPES);
        } else {
            return session.nodeExists(PUBLICATION_VALUE_LIST) ?  session.getNode(PUBLICATION_VALUE_LIST) : null;
        }
    }

    Node publicationTypesForSiteNode(HstRequestContext context) throws RepositoryException {
        Node typesForSiteNode = context.getSession().getNode("/content/publicationtypes");
        String sitename = context.getSiteContentBaseBean().getName();
        return typesForSiteNode.hasNode(sitename) ? typesForSiteNode.getNode(sitename) : null;
    }

}
