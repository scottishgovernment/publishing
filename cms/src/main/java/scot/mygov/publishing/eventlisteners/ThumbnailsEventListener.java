package scot.mygov.publishing.eventlisteners;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.gov.imageprocessing.exif.Exif;
import scot.gov.imageprocessing.thumbnails.FileType;
import scot.gov.imageprocessing.thumbnails.ThumbnailsProvider;
import scot.gov.imageprocessing.thumbnails.ThumbnailsProviderException;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.*;
import javax.jcr.Binary;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hippoecm.repository.api.HippoNodeType.HIPPO_FILENAME;
import static org.onehippo.repository.util.JcrConstants.JCR_DATA;
import static org.onehippo.repository.util.JcrConstants.JCR_MIME_TYPE;

/**
 * Listen for document uploads in order to create thumbnails.
 */
public class ThumbnailsEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(ThumbnailsEventListener.class);

    static final String HASH_PROPERTY = "publishing:hash";

    private Session session;

    HippoUtils hippoUtils = new HippoUtils();

    ThumbnailsProvider thumbnailsProvider = new ThumbnailsProvider();

    Exif exif = new Exif();

    ExecutorService executor = Executors.newSingleThreadExecutor();

    interface HashProvider {
        String hash(Node node) throws RepositoryException, IOException;
    }

    HashProvider hashProvider = node -> {
        InputStream is = node.getProperty("jcr:data").getBinary().getStream();
        return  DigestUtils.md5Hex(is);
    };

    public ThumbnailsEventListener(Session session) {
        this.session = session;
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {
        if (!canHandleEvent(event)) {
            return;
        }

        executor.submit(() -> doHandleEventWithLogging(event));

    }

    public boolean canHandleEvent(HippoWorkflowEvent event) {
        if (!event.success()) {
            return false;
        }

        return "upload".equals(event.action());
    }

    void doHandleEventWithLogging(HippoWorkflowEvent event) {
        try {
            Node node = session.getNodeByIdentifier(event.subjectId());
            ensureThumbnail(node);
        } catch (Exception e) {
            LOG.error("{} Unexpected exception while trying to update thumbnail, subject path is {}",
                    this.getClass().getName(), event.subjectPath(), e);
        }
    }

    void ensureThumbnail(Node resource) throws  IOException, ThumbnailsProviderException, RepositoryException {
        Node document = resource.getParent();
        String currentHash = currentHash(document);
        String newHash = hashProvider.hash(resource);
        if (newHash.equals(currentHash)) {
            return;
        }
        deleteExistingThumbnails(document);
        createThumbnails(resource, document);
        document.setProperty(HASH_PROPERTY, newHash);
        session.save();
    }

    private void createThumbnails(Node resource, Node document)
            throws RepositoryException, FileNotFoundException, ThumbnailsProviderException {

        Binary data = resource.getProperty(JCR_DATA).getBinary();
        String mimeType = resource.getProperty(JCR_MIME_TYPE).getString();
        String filename = resource.getProperty(HIPPO_FILENAME).getString();
        Map<Integer, File> thumbnails = thumbnailsProvider.thumbnails(data.getStream(), mimeType);
        List<Integer> sortedKeys = new ArrayList<>(thumbnails.keySet());
        Collections.sort(sortedKeys);
        for (Integer size : sortedKeys) {
            createThumbnail(document, filename, size, thumbnails.get(size));
        }
        document.setProperty("publishing:size", data.getSize());
        setPageCount(document, data, mimeType);
    }

    void createThumbnail(Node document, String filename, Integer size, File thumbnail) throws RepositoryException, FileNotFoundException  {
        Node resourceNode = document.addNode("publishing:thumbnails", "hippo:resource");
        resourceNode.addMixin("hippo:skipindex");
        Binary binary = session.getValueFactory().createBinary(new FileInputStream(thumbnail));
        String thumbnailFilename = String.format("%s_%s.png", filename, size);
        resourceNode.setProperty("hippo:filename", thumbnailFilename);
        resourceNode.setProperty("jcr:data", binary);
        resourceNode.setProperty("jcr:mimeType", FileType.PNG.getMimeType());
        resourceNode.setProperty("jcr:lastModified", Calendar.getInstance());
        FileUtils.deleteQuietly(thumbnail);
    }

    void setPageCount(Node document, Binary data, String mimeType) throws RepositoryException {
        if (FileType.forMimeType(mimeType) == FileType.PDF) {
            document.setProperty("publishing:pageCount", exif.pageCount(data));
        } else {
            document.setProperty("publishing:pageCount", 0);
        }
    }

    private void deleteExistingThumbnails(Node document) throws RepositoryException {
        hippoUtils.apply(
                document.getNodes("publishing:thumbnails"),
                Node::remove);
    }

    String currentHash(Node document) throws RepositoryException {
        return document.hasProperty(HASH_PROPERTY)
                ? document.getProperty(HASH_PROPERTY).getString()
                : "";
    }

}