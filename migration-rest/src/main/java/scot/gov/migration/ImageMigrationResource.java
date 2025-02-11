package scot.gov.migration;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.frontend.plugins.gallery.processor.ScalingGalleryProcessor;
import org.hippoecm.repository.api.HippoNodeType;
import org.hippoecm.repository.gallery.HippoGalleryNodeType;
import org.hippoecm.frontend.plugins.gallery.imageutil.*;
import org.hippoecm.repository.util.JcrUtils;
import org.onehippo.repository.util.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ImageMigrationResource {

    private static final Logger LOG = LoggerFactory.getLogger(ImageMigrationResource.class);

    private static final String IMAGE = "hippogallery:image";
    private static final String IMAGE_SET_THUMBNAIL = "hippogallery:thumbnail";
    private static final String IMAGE_SET_ORIGINAL = "hippogallery:original";

    private static final String HIPPO_CONFIGURATION_GALLERY_PROCESSOR_SERVICE = "/hippo:configuration/hippo:frontend/cms/cms-services/galleryProcessorService";

    private static final int DEFAULT_WIDTH = 0;
    private static final int DEFAULT_HEIGHT = 0;
    private static final boolean DEFAULT_UPSCALING = false;
    private static final boolean DEFAULT_CROPPING = false;
    private static final String DEFAULT_OPTIMIZE = "quality";
    private static final double DEFAULT_COMPRESSION = 1.0;

    private static final String CONFIG_PARAM_WIDTH = "width";
    private static final String CONFIG_PARAM_HEIGHT = "height";
    private static final String CONFIG_PARAM_UPSCALING = "upscaling";
    private static final String CONFIG_PARAM_CROPPING = "cropping";
    private static final String CONFIG_PARAM_OPTIMIZE = "optimize";
    private static final String CONFIG_PARAM_COMPRESSION = "compression";

    private static final Set<String> VARIANTS_TO_IGNORE = new HashSet<>();

    static {
        VARIANTS_TO_IGNORE.add(HippoGalleryNodeType.IMAGE_SET_ORIGINAL);
        VARIANTS_TO_IGNORE.add(HippoGalleryNodeType.IMAGE_SET_THUMBNAIL);
    }

    private final Map<String, ScalingParameters> imageVariantParameters = new HashMap<>();

    private static final Map<String, ImageUtils.ScalingStrategy> SCALING_STRATEGY_MAP = new LinkedHashMap<>();

    static {
        SCALING_STRATEGY_MAP.put("auto", ImageUtils.ScalingStrategy.AUTO);
        SCALING_STRATEGY_MAP.put("speed", ImageUtils.ScalingStrategy.SPEED);
        SCALING_STRATEGY_MAP.put("speed.and.quality", ImageUtils.ScalingStrategy.SPEED_AND_QUALITY);
        SCALING_STRATEGY_MAP.put(DEFAULT_OPTIMIZE, ImageUtils.ScalingStrategy.QUALITY);
        SCALING_STRATEGY_MAP.put("best.quality", ImageUtils.ScalingStrategy.BEST_QUALITY);
    }

    Session daemonSession;

    MigrationUserCredentialsSource credentialsSource;

    public ImageMigrationResource(Session daemonSession, MigrationUserCredentialsSource credentialsSource) {
        this.daemonSession = daemonSession;
        this.credentialsSource = credentialsSource;
    }

    /**
     * Recalculate image variants for a content type
     */
    @POST
    @Path("/resize/{site}/{contentType}")
    @Produces(MediaType.APPLICATION_JSON)
    public String generateVariants(
            @PathParam("site") String site,
            @PathParam("contentType") String contentType,
            @HeaderParam("Authorization") String authHeader,
            @Context UriInfo uriInfo) {
        Session session = null;
        try {
            session = session(authHeader);

            getImageVariantParametersFromProcessor(session);
            ImageSet imageSet = getImageSet(session, contentType);
            processImages(session, site, contentType, imageSet);
            return "Done";
        } catch (RepositoryException e) {
            LOG.error("Failed to generate variants", e);
            throw new WebApplicationException("Server error", 500);
        } finally {
            logoutSafely(session);
        }
    }

    void processImages(Session session, String site, String contentType, ImageSet imageSet) throws RepositoryException {
        String query = String.format("/jcr:root/content/gallery/%s//element(*, %s)", site, contentType);
        Query queryObj = session.getWorkspace().getQueryManager().createQuery(query, Query.XPATH);
        queryObj.setLimit(10000);
        QueryResult result = queryObj.execute();
        NodeIterator nodeIt = result.getNodes();
        while (nodeIt.hasNext()) {
            Node node = nodeIt.nextNode();
            processImageSet(node, imageSet);
        }
    }

    ImageSet getImageSet(Session session, String name) throws RepositoryException {
        Node prototype = findProtoType(name, session);
        if (prototype == null) {
            return null;
        }

        Node docType = prototype.getParent().getParent();
        String relNodeTypePath = HippoNodeType.HIPPOSYSEDIT_NODETYPE + "/" + HippoNodeType.HIPPOSYSEDIT_NODETYPE;
        Node nodeType = docType.hasNode(relNodeTypePath) ? docType.getNode(relNodeTypePath) : null;
        if (nodeType == null) {
            return null;
        }

        ImageSet imageSet = new ImageSet();
        addSuperTypes(nodeType, imageSet);
        NodeIterator fields = nodeType.getNodes();
        while (fields.hasNext()) {
            Node field = fields.nextNode();
            addVariants(field, docType, imageSet);
        }
        return imageSet;
    }

    void addVariants(Node field, Node docType, ImageSet imageSet) throws RepositoryException{
        if (!field.hasProperty(HippoNodeType.HIPPOSYSEDIT_TYPE)) {
            return;
        }

        if (!IMAGE.equals(field.getProperty(HippoNodeType.HIPPOSYSEDIT_TYPE).getString())) {
            return;
        }

        String variantName = (field.hasProperty(HippoNodeType.HIPPO_PATH)) ?
                field.getProperty(HippoNodeType.HIPPO_PATH).getString() :
                docType.getParent().getName() + ":" + field.getName();

        if (IMAGE_SET_ORIGINAL.equals(variantName)) {
            return;
        }

        imageSet.variants.add(variantName);
    }


    void addSuperTypes(Node nodetype, ImageSet imageSet) throws RepositoryException {
        if (!nodetype.hasProperty(HippoNodeType.HIPPO_SUPERTYPE)) {
            return;
        }
        Value[] values = nodetype.getProperty(HippoNodeType.HIPPO_SUPERTYPE).getValues();
        for (Value v : values) {
            if (!v.getString().startsWith("hippogallery")) {
                imageSet.superTypes.add(v.getString());
            }
        }
    }

    Node findProtoType(String name, Session session) throws RepositoryException {
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        String nameNoNamespace = StringUtils.substringAfter(name, ":");
        String xpath = String.format("/jcr:root/hippo:namespaces/publishing/%s//element(*, %s)", nameNoNamespace, name);
        Query query = queryManager.createQuery(xpath, "xpath");
        QueryResult queryResult = query.execute();
        NodeIterator nodeIterator = queryResult.getNodes();
        return nodeIterator.hasNext() ? nodeIterator.nextNode() : null;
    }

    void processImageSet(Node node, ImageSet imageSet) throws RepositoryException {
        Node data = node.hasNode(IMAGE_SET_ORIGINAL) ? node.getNode(IMAGE_SET_ORIGINAL) : node.getNode(IMAGE_SET_THUMBNAIL);
        LOG.info("processImageSet {}", node.getPath());
        for (String variantName : imageSet.variants) {
            processImageVariant(node, data, variantName);
        }
        node.getSession().save();
    }

    void processImageVariant(Node node, Node data, String variantName) throws RepositoryException {
        if (IMAGE_SET_ORIGINAL.equals(variantName)) {
            return;
        }

        if (node.hasNode(variantName)) {
            LOG.debug("node already has variant {}, {}", node.getPath(), variantName);
            return;
        }

        ScalingParameters parameters = imageVariantParameters.get(variantName);
        if (parameters == null) {
            LOG.warn("No parameters found for image variant {}. Skipping variant for node {}", variantName, node.getPath());
            return;
        }

        Node variant = node.addNode(variantName, IMAGE);
        createImageVariant(node, data, variant, parameters);
    }

    void createImageVariant(Node node, Node data, Node variant, ScalingParameters parameters) throws RepositoryException {
        if (!data.hasProperty(JcrConstants.JCR_DATA)) {
            LOG.warn("Image variant {} for node {} does not have {} property. Variant not updated.",
                    variant.getName(), node.getPath(), JcrConstants.JCR_DATA);
            return;
        }

        try (InputStream dataInputStream = data.getProperty(JcrConstants.JCR_DATA).getBinary().getStream()) {
            String mimeType = data.getProperty(JcrConstants.JCR_MIME_TYPE).getString();
            ScalingGalleryProcessor scalingGalleryProcessor = new ScalingGalleryProcessor();
            scalingGalleryProcessor.addScalingParameters(variant.getName(), parameters);
            scalingGalleryProcessor.initGalleryResource(variant, dataInputStream, mimeType, "", Calendar.getInstance());
        } catch (IOException e) {
            LOG.error("failed to read data", e);
        }
    }


    void getImageVariantParametersFromProcessor(Session session) throws RepositoryException {
        Node configNode = session.getNode(HIPPO_CONFIGURATION_GALLERY_PROCESSOR_SERVICE);
        NodeIterator variantNodes = configNode.getNodes();
        while (variantNodes.hasNext()) {
            Node variantNode = variantNodes.nextNode();
            mapVariant(variantNode);
        }
    }

    void mapVariant(Node variantNode) throws RepositoryException {
        String variantName = variantNode.getName();

        if (VARIANTS_TO_IGNORE.contains(variantName)) {
            return;
        }

        int width = variantNode.hasProperty(CONFIG_PARAM_WIDTH) ? (int) variantNode.getProperty(CONFIG_PARAM_WIDTH).getLong() : DEFAULT_WIDTH;
        int height = variantNode.hasProperty(CONFIG_PARAM_HEIGHT) ? (int) variantNode.getProperty(CONFIG_PARAM_HEIGHT).getLong() : DEFAULT_HEIGHT;
        if (width == 0 && height == 0) {
            return;
        }

        ScalingParameters parameters = getScalingParameters(variantNode, width, height);
        imageVariantParameters.put(variantName, parameters);
    }

    ScalingParameters getScalingParameters(Node variantNode, int width, int height) throws RepositoryException {
        boolean upscaling = JcrUtils.getBooleanProperty(variantNode, CONFIG_PARAM_UPSCALING, DEFAULT_UPSCALING);
        boolean cropping = JcrUtils.getBooleanProperty(variantNode, CONFIG_PARAM_CROPPING, DEFAULT_CROPPING);
        String optimize = JcrUtils.getStringProperty(variantNode, CONFIG_PARAM_OPTIMIZE, DEFAULT_OPTIMIZE);
        double compression = JcrUtils.getDoubleProperty(variantNode, CONFIG_PARAM_COMPRESSION, DEFAULT_COMPRESSION);
        ImageUtils.ScalingStrategy strategy = SCALING_STRATEGY_MAP.get(optimize);
        if (strategy == null) {
            strategy = SCALING_STRATEGY_MAP.get(DEFAULT_OPTIMIZE);
        }
        return new ScalingParameters(width, height, upscaling, cropping, strategy, (float) compression);
    }

    Session session(String authHeader) throws RepositoryException {
        Credentials credentials = credentialsSource.get(authHeader);
        return daemonSession.impersonate(credentials);
    }

    void logoutSafely(Session session) {
        if (session == null) {
            return;
        }

        session.logout();
    }

    static class ImageSet {
        List<String> superTypes = new ArrayList<>();
        List<String> variants = new ArrayList<>();
    }
}