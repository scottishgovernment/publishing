package scot.mygov.publishing.linkprocessors;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.util.Text;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.publishing.sluglookup.PathForSlugSource;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

public class QueryPathSource implements PathForSlugSource {

    private static final Logger LOG = LoggerFactory.getLogger(QueryPathSource.class);

    @Override
    public String get(String slug, String site, String type, String mount) throws RepositoryException {

        LOG.warn("falling back to QueryPathSource, {} {} {} {}", slug, site, type, mount);
        HstRequestContext req = RequestContextProvider.get();
        Session session = req.getSession();
        String escapedSlug = Text.escapeIllegalJcr10Chars(slug);
        String template =
                "/jcr:root/content/documents/%s//element(*, publishing:base)[publishing:slug = '%s']";
        String xpath = String.format(template, site, escapedSlug);
        QueryResult result = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH).execute();
        if (result.getNodes().getSize() == 0) {
            return null;
        }

        // find the index in the results folder
        Node publishedNode = findPublishedNode(result.getNodes());
        if (publishedNode == null) {
            return null;
        }

        return StringUtils.substringAfter(publishedNode.getParent().getPath(), "/content/documents/" + site);
    }

    protected Node findPublishedNode(NodeIterator nodeIterator) throws RepositoryException {
        Node publishedNode = null;
        Node lastNode = null;
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            lastNode = node;
            if (isPublished(node)) {
                publishedNode = node;
            }
        }
        return publishedNode != null ? publishedNode : lastNode;
    }

    boolean isPublished(Node node) throws RepositoryException {
        return node.isNodeType("publishing:base")
                && "published".equals(node.getProperty("hippostd:state").getString());
    }
}