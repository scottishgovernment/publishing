package scot.mygov.publishing.validation;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.frontend.session.UserSession;

import javax.jcr.*;
import javax.jcr.query.Query;
import java.util.Arrays;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isAlphanumeric;

/**
 * Shared code for validating slugs and urls.
 */
public class UrlValidationUtils {

    Supplier<Session> sessionSupplier = () -> UserSession.get().getJcrSession();

    public boolean pathElementsContainInvalidCharacter(String path) {
        return Arrays.stream(path.split("/")).filter(StringUtils::isNotBlank).anyMatch(this::containsInvalidCharacters);
    }

    public boolean containsInvalidCharacters(String slug) {
        String withoutHyphens = slug.replace("-", "");
        String lowercase = withoutHyphens.toLowerCase();
        return !isAlphanumeric(lowercase) || !lowercase.equals(withoutHyphens);
    }

    /**
     * The slug is slugClashes if no other nodes share this slug
     * (with the exception of ones that share the same handle).
     */
    public String slugClashes(String slug, Node documentNode) throws RepositoryException {
        NodeIterator it = getNodesWithSlug(slug, documentNode);
        return firstWithNonSharedParent(documentNode, it);
    }

    public String slugClashesWithAlias(String slug, Node documentNode) throws RepositoryException {
        NodeIterator it = getNodesWithAlias("/" + slug, documentNode);
        return firstPathIfNotEmpty(it);
    }

    public String aliasClashesWithSlug(String alias, Node documentNode) throws RepositoryException {
        if (!isSingleElementUrl(alias)) {
            return null;
        }

        String slug = withoutLeadingSlash(alias);
        NodeIterator it = getNodesWithSlug(slug, documentNode);
        return firstPathIfNotEmpty(it);
    }

    public String aliasClashesWithAnotherAlias(String alias, Node documentNode) throws RepositoryException {
        NodeIterator it = getNodesWithAlias(alias, documentNode);
        return firstWithNonSharedParent(documentNode, it);
    }

    public String aliasClashesWithCategory(String alias, Node documentNode) throws RepositoryException {
        Session session = sessionSupplier.get();
        String sitePath = sitePath(documentNode);
        String testPath = sitePath + alias;

        Node clash = session.nodeExists(testPath) ? session.getNode(testPath) : null;
        if (clash != null && isCategory(clash)) {
            return clash.getPath();
        } else {
            return null;
        }
    }

    public String slugClashesWithCategory(String alias, Node documentNode) throws RepositoryException {
        return aliasClashesWithCategory("/" + alias, documentNode);
    }

    boolean isSingleElementUrl(String alias) {
        String withoutLeadingSlash = withoutLeadingSlash(alias);
        return !withoutLeadingSlash.contains("/");
    }

    String withoutLeadingSlash(String str) {
        return StringUtils.substringAfter(str, "/");
    }

    boolean isCategory(Node clash) throws RepositoryException {
        if (!clash.isNodeType("hippostd:folder")) {
            return false;
        }

        Node handle = clash.getNode("index");
        NodeIterator variants = handle.getNodes(handle.getName());
        Node firstVariant = variants.nextNode();
        return firstVariant.isNodeType("publishing:category");
    }

    private NodeIterator getNodesWithSlug(String slug, Node node) throws RepositoryException {
        return executeQuery(slugSQL(slug, node));
    }

    private NodeIterator getNodesWithAlias(String alias, Node node) throws RepositoryException {
        return executeQuery(aliasSQL(alias, node));
    }

    private NodeIterator executeQuery(String sql) throws RepositoryException {
        Session session = sessionSupplier.get();
        Query query = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
        return query.execute().getNodes();
    }

    private static String firstWithNonSharedParent(Node node, NodeIterator it) throws RepositoryException {
        String parentId = node.getParent().getIdentifier();
        while (it.hasNext()) {
            Node next = it.nextNode();
            if (!sharedParent(parentId, next)) {
                return next.getParent().getPath();
            }
        }
        return null;
    }

    private static boolean sharedParent(String parentId, Node node) throws RepositoryException {
        String nodeParentId = node.getParent().getIdentifier();
        return parentId.equals(nodeParentId);
    }

    private static String slugSQL(String slug, Node node) throws RepositoryException {
        return sql(slug, "publishing:slug", node);
    }

    private static String aliasSQL(String slug, Node node) throws RepositoryException {
        return sql(slug, "publishing:urlAliases", node);
    }

    private static String sql(String slug, String property, Node node) throws RepositoryException {
        String template = "SELECT * FROM publishing:base WHERE %s = '%s' AND jcr:path LIKE '%s/%%'";
        return String.format(template, property, slug, sitePath(node));
    }

    private static String sitePath(Node node) throws RepositoryException {

        // the site path is the path to the site which we can get from the first 3 path elements
        // e.g. /content/documents/trading-nation
        Item siteNode = node.getAncestor(3);
        return siteNode.getPath();
    }

    String firstPathIfNotEmpty(NodeIterator it) throws RepositoryException {
        return it.getSize() > 0 ? it.nextNode().getParent().getPath() : null;
    }

}
