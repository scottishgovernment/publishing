package scot.gov.migration;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.onehippo.forge.content.exim.core.ContentMigrationException;
import org.onehippo.forge.content.pojo.model.ContentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.*;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static java.io.File.createTempFile;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.onehippo.repository.util.JcrConstants.*;

/**
 * Rest resource to allow migrations to publish content.
 */
public class MigrationResource {

    private static final Logger LOG = LoggerFactory.getLogger(MigrationResource.class);

    private static final String PUBLISHING_DOCUMENT = "publishing:document";

    private static final String TITLE = "publishing:title";

    Session daemonSession;

    MigrationUserCredentialsSource credentialsSource;

    DocumentUpdater documentUpdater = new DocumentUpdater();

    HippoUtils hippoUtils = new HippoUtils();

    public MigrationResource(Session daemonSession, MigrationUserCredentialsSource credentialsSource) {
        this.daemonSession = daemonSession;
        this.credentialsSource = credentialsSource;
    }


    /**
     * Publish a ContentNode for a given site and path.
     */
    @POST
    @Path("/{site}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Result newPublishingDocument(
            ContentNode contentNode,
            @PathParam("site") String site,
            @QueryParam("path") String path,
            @QueryParam("publish") @DefaultValue("true") boolean publish,
            @HeaderParam("Authorization") String authHeader,
            @Context UriInfo uriInfo) {
        LOG.info("newPublishingDocument {}, {}, {}", contentNode.getName(), site, path);
        Session session = null;
        try {
            session = session(authHeader);
            ContentAuthorship authorship
                    = ContentAuthorship.newInstance(uriInfo.getQueryParameters());
            String location = documentUpdater.update(
                    session,
                    site,
                    path,
                    publish,
                    contentNode,
                    authorship);
            return new Result(location);
        } catch (ContentMigrationException e) {
            LOG.error("Failed to create items", e);
            throw new WebApplicationException("Client error", 400);
        } catch (RepositoryException e) {
            LOG.error("Invalid item", e);
            throw new WebApplicationException("Servver error", 500);
        } finally {
            logoutSafely(session);
        }
    }

    @POST
    @Path("/documents")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public DocumentResult uploadDocument(DocumentRequest documentRequest, @HeaderParam("Authorization") String authHeader) {
        Session session = null;

        try {
            session = session(authHeader);
            upload(session, documentRequest);
            return new DocumentResult("done");
        } catch (IOException e) {
            LOG.error("Failed to create items", e);
            return errorResult(e);
        } catch (RepositoryException e) {
            LOG.error("Invalid item", e);
            return errorResult(e);
        } finally {
            logoutSafely(session);
        }
    }

    DocumentResult errorResult(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return new DocumentResult(sw.toString());
    }


    void upload(Session session, DocumentRequest request) throws RepositoryException, IOException {

        Node handle = session.getNode(request.getPath());
        Binary binary = getCleanedBinary(session, new URL(request.getUrl()), request.getContentType());
        hippoUtils.apply(handle.getNodes(),
                    variant -> uploadDocumentsAndCreateThumbnailsForVariant(session, variant, request, binary));

        String filenameIn = request.getFilename().replace(" ", "%20").toLowerCase();
        String oldUrl = "/" + request.getSlug() + "/" + filenameIn;

        // filename out has been encoded...
        String filenameOut = java.net.URLEncoder.encode(request.getFilename(), "UTF-8");
        String newUrl = String.format("/binaries/%s/%s",
                substringAfter(handle.getPath(), "/content/documents/"),
                filenameOut);
        Node rewritesFolder = session.getNode(request.getRewritesFolder());
        createRewriteRule(rewritesFolder, oldUrl, newUrl);
        session.save();
    }

    Binary getCleanedBinary(Session session, URL url, String mimetype) throws IOException, RepositoryException {
        File tmp = createTempFile("MigrationResource", "FileUpload");
        try {
            FileOutputStream out = new FileOutputStream(tmp);

            if (isDocx(mimetype) || isXlsx(mimetype)) {
                POIXMLDocument poiDoc = getDoc(url, mimetype);
                POIXMLProperties.CoreProperties coreProps = poiDoc.getProperties().getCoreProperties();
                coreProps.setCreator("");
                coreProps.setLastModifiedByUser("");
                poiDoc.write(out);
            } else if (isXls(mimetype)) {
                HSSFWorkbook xls = getOldExcel(url);
                xls.getSummaryInformation().setAuthor("");
                xls.getSummaryInformation().setLastAuthor("");
            } else if (isDoc(mimetype)) {
                HWPFDocument word = getOldDoc(url);
                word.getSummaryInformation().setAuthor("");
                word.getSummaryInformation().setLastAuthor("");
            }

            InputStream in = new FileInputStream(tmp);
            return session.getValueFactory().createBinary(in);

        } finally {
            FileUtils.deleteQuietly(tmp);
        }
    }

    POIXMLDocument getDoc(URL url, String mimetype) throws RepositoryException, IOException {
        if (isXlsx(mimetype)) {
            return new XSSFWorkbook(url.openStream());
        }

        if (isDocx(mimetype)) {
            return new XWPFDocument(url.openStream());
        }

        return null;
    }

    HWPFDocument getOldDoc(URL url) throws RepositoryException, IOException {
        return new HWPFDocument(url.openStream());
    }

    HSSFWorkbook getOldExcel(URL url) throws RepositoryException, IOException {
        return new HSSFWorkbook(url.openStream());
    }

    boolean isXlsx(String mimetype) {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(mimetype);
    }

    boolean isXls(String mimetype) {
        return "application/msexcel".equals(mimetype);
    }

    boolean isDoc(String mimetype) {
        return "application/msword".equals(mimetype);
    }

    boolean isDocx(String mimetype) {
        return "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimetype);
    }

    void uploadDocumentsAndCreateThumbnailsForVariant(
            Session session,
            Node variant,
            DocumentRequest document,
            Binary binary) throws RepositoryException {

        Node documentsNode = hippoUtils.createNode(variant,
                "publishing:documents", PUBLISHING_DOCUMENT,
                "hippo:container", "hippostd:container", "hippostd:relaxed");
        documentsNode.setProperty(TITLE, document.getFilename());
        documentsNode.setProperty("publishing:size", binary.getSize());
        documentsNode.setProperty("publishing:pageCount", document.getPageCount());

        Node documentNode = hippoUtils.createNode(documentsNode, PUBLISHING_DOCUMENT, "hippo:resource");
        Calendar lastModified = GregorianCalendar.getInstance();
        lastModified.setTimeInMillis(document.getCreatedDate());
        documentNode.setProperty("hippo:filename", document.getFilename());
        documentNode.setProperty("jcr:data", binary);
        documentNode.setProperty(JCR_LAST_MODIFIED, lastModified);
        documentNode.setProperty(JCR_MIME_TYPE, document.getContentType());
        documentNode.setProperty(JCR_ENCODING, "UTF-8");

        try {
            createThumbnail(session, documentsNode, document.getFilename(), 330, document.getUrl());
            createThumbnail(session, documentsNode, document.getFilename(), 214, document.getUrl());
            createThumbnail(session, documentsNode, document.getFilename(), 165, document.getUrl());
            createThumbnail(session, documentsNode, document.getFilename(), 330, document.getUrl());
            createThumbnail(session, documentsNode, document.getFilename(), 107, document.getUrl());
        } catch (IOException e) {
            LOG.warn("Failed to generate thumbnail", e);
        }
    }

    void createThumbnail(Session session, Node document, String filename, Integer size, String docurl) throws RepositoryException, IOException {
        String thumbUrl = docurl + "?size="+size;
        Node resourceNode = document.addNode("publishing:thumbnails", "hippo:resource");
        resourceNode.addMixin("hippo:skipindex");

        try (InputStream inputStream = new URL(thumbUrl).openStream()) {
            Binary binary = session.getValueFactory().createBinary(inputStream);
            String thumbnailFilename = String.format("%s_%s.png", filename, size);
            resourceNode.setProperty("hippo:filename", thumbnailFilename);
            resourceNode.setProperty("jcr:data", binary);
            resourceNode.setProperty("jcr:mimeType", "image/png");
            resourceNode.setProperty("jcr:lastModified", Calendar.getInstance());
        }
    }

    void createRewriteRule(Node folder, String from, String to) throws RepositoryException {
        LOG.info("createRewriteRule {}, {}, {}", folder.getPath(), from, to);
        String name = from.replaceAll("/", "").trim();
        Node handle = hippoUtils.createNode(folder, name, "hippo:handle", MIX_REFERENCEABLE, "hippo:versionInfo");
        Node rule = hippoUtils.createNode(handle, name, "urlrewriter:rule", "mix:referenceable", "hippo:container",
                "hippo:derived", "hippostd:container", "hippostd:publishable", "hippostd:publishableSummary", "hippostd:relaxed",
                "hippostdpubwf:document");
        rule.setProperty("hippostdpubwf:createdBy", "migration");
        rule.setProperty("hippostdpubwf:creationDate", Calendar.getInstance());
        rule.setProperty("hippostdpubwf:lastModificationDate", Calendar.getInstance());
        rule.setProperty("hippostdpubwf:publicationDate", Calendar.getInstance());
        rule.setProperty("hippostdpubwf:lastModifiedBy", "migration");
        rule.setProperty("urlrewriter:ruledescription", from);
        rule.setProperty("urlrewriter:rulefrom", from);
        rule.setProperty("urlrewriter:ruleto", to);
        rule.setProperty("urlrewriter:ruletype", "permanent-redirect");
        rule.setProperty("hippo:availability", new String [] {"live"});
        rule.setProperty("hippostd:state", "published");
        rule.setProperty("hippostd:stateSummary", "live");
    }

    /**
     * Impersonate the session used to initiate this module but use the redentials of the user who is posting
     * the content.
     */
    private Session session(String authHeader) throws RepositoryException {
        Credentials credentials = credentialsSource.get(authHeader);
        return daemonSession.impersonate(credentials);
    }

    /**
     * Best effort to ensure that the session is closed.
     ***/
    private void logoutSafely(Session session) {
        if (session == null) {
            return;
        }

        session.logout();
    }

    /**
     * Class to represent the result.
     */
    static class Result {

        String path;

        public Result(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }


    }

    static class RewriteResult {
        List<String> rewrites;

        public List<String> getRewrites() {
            return rewrites;
        }

        public void setRewrites(List<String> rewrites) {
            this.rewrites = rewrites;
        }
    }
}
