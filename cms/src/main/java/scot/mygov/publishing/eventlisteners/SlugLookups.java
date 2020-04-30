package scot.mygov.publishing.eventlisteners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import static org.apache.commons.lang.StringUtils.substringAfter;

/**
 * Contains logic used by SlugMaintenanceListener to update and remove lookups.
 */
public class SlugLookups {

    private static final Logger LOG = LoggerFactory.getLogger(SlugLookups.class);

    private static final String PATH = "publishing:path";

    Session session;

    public SlugLookups(Session session) {
        this.session = session;
    }

    public void updateLookup(Node subject, String mountType) throws RepositoryException {
        String site = sitename(subject);
        String slug = slug(subject);
        String path = substringAfter(subject.getPath(), site);

        // check if there is an existing lookup for this path
        Node existingLookup = findLookupForPath(site, mountType, path);
        if (existingLookup != null) {

            // check if there is an existing lookup that is the same as the one we would expect
            // if soo then no action is required.
            if (isExpectedPath(existingLookup.getPath(), site, mountType, path)) {
                return;
            }

            // clear the existing lookup
            clearLookup(existingLookup);
        }

        // ensure that the lookup has been created for the current slug
        ensureLookupPath(site, mountType, slug, path);
        session.save();
    }

    public void removeLookup(Node subject, String mountType) throws RepositoryException {
        String site = sitename(subject);
        Node existingLookup = findLookupForPath(site, mountType, subject.getPath());
        clearLookup(existingLookup);
    }

    private Node findLookupForPath(String site, String mountType, String path) throws RepositoryException {
        String xpath = xpathFindLookupByPath(site, mountType, path);
        NodeIterator it = execute(xpath);

        if (it.getSize() == 0) {
            return null;
        }

        if (it.getSize() > 1) {
            LOG.warn("More than one entry for {}", path);
        }

        return it.nextNode();
    }

    private String xpathFindLookupByPath(String site, String mountType, String path) {
        String template =
                "/jcr:root/content/urls/%s/%s" +
                        "//element(*, nt:unstructured)[publishing:path = '%s']";
        return String.format(template, site, mountType, path);
    }

    private String sitename(Node subject) throws RepositoryException {
        return subject.getAncestor(3).getName();
    }

    private String slug(Node subject) throws RepositoryException {
        return subject.getNode(subject.getName()).getProperty("publishing:slug").getString();
    }

    private boolean isExpectedPath(String existingPath, String site, String mountType, String slug) {
        String slugLookupPath = slugLookupPath(site, mountType, slug);
        return slugLookupPath.equals(existingPath);
    }

    private String slugLookupPath(String site, String mountType, String slug) {
        StringBuilder b = new StringBuilder()
                .append("/content/urls/")
                .append(site)
                .append('/')
                .append(mountType)
                .append('/');
        appendLettersPath(b, slug);
        return b.toString();
    }

    private NodeIterator execute(String xpath) throws RepositoryException {
        return session
                .getWorkspace()
                .getQueryManager()
                .createQuery(xpath, Query.XPATH)
                .execute()
                .getNodes();
    }

    private StringBuilder appendLettersPath(StringBuilder b, String slug) {
        for(char c : slug.toCharArray()) {
            b.append(c);
            b.append('/');
        }
        return b;
    }

    private Node ensureLookupPath(String site, String mountType, String slug, String path) throws RepositoryException {
        Node parent = session.getNode("/content/urls").getNode(site).getNode(mountType);
        Node lookup = ensureLookupPath(parent, 0, slug);
        lookup.setProperty(PATH, path);
        return lookup;
    }

    private Node ensureLookupPath(Node parent, int pos, String path) throws RepositoryException {

        if (pos == path.length()) {
            return parent;
        }

        String element = Character.toString(path.charAt(pos));
        Node next = parent.hasNode(element)
                ? parent.getNode(element)
                : redirectNode(parent, element);
        int newPos = pos + 1;
        return ensureLookupPath(next, newPos, path);
    }

    private Node redirectNode(Node parent, String name) throws RepositoryException {
        return parent.addNode(name, "nt:unstructured");
    }

    void clearLookup(Node node) throws RepositoryException {
        node.getProperty(PATH).remove();
    }
}
