package scot.mygov.publishing.advancedsearch.workflow;

import com.onehippo.cms7.search.frontend.ISearchContext;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ResourceLink;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.AbstractResource;
import org.apache.wicket.request.resource.ByteArrayResource;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.CssResourceReference;
import org.hippoecm.frontend.dialog.Dialog;
import org.hippoecm.frontend.dialog.DialogConstants;
import org.hippoecm.frontend.plugins.standards.datetime.DateTimePrinter;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.platform.model.HstModel;
import org.hippoecm.hst.platform.model.HstModelRegistry;
import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.api.WorkflowDescriptor;
import org.onehippo.cms7.channelmanager.HstUtil;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.*;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.hippoecm.repository.HippoStdNodeType.*;
import static org.hippoecm.repository.HippoStdPubWfNodeType.*;
import static org.hippoecm.repository.api.HippoNodeType.HIPPO_DOCBASE;

public class ExportDialog extends Dialog<WorkflowDescriptor> {

    private static final Logger LOG = LoggerFactory.getLogger(ExportDialog.class);
    private static final CssResourceReference CSS = new CssResourceReference(ExportDialog.class, "ExportDialog.css");
    private static final byte[] BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final String REVIEW_DATE_PROP = "publishing:reviewDate";
    private static final String INDEX = "index";
    private static final String PUBLISHING_DOCUMENT = "publishing:document";

    @FunctionalInterface
    interface FieldExtractor {
        String extract(Node variant, String stateSummary) throws RepositoryException;
    }

    static class CsvField {
        final String header;
        final FieldExtractor extractor;

        CsvField(String header, FieldExtractor extractor) {
            this.header = header;
            this.extractor = extractor;
        }
    }

    private final ISearchContext searcher;

    public ExportDialog(final ISearchContext searcher) {
        super();
        this.searcher = searcher;
        int documentsCount = getCountSelectedDocuments();

        Label countText = new Label("counttext", MessageFormat.format(
                new StringResourceModel("export-confirmation-subtext",
                        ExportDialog.this).getString(), documentsCount
        ));
        add(countText);

        ResourceLink<String> exportCSVLink = new ResourceLink<String>("exportCSV", getResource()) {
            @Override
            public boolean isEnabled() {
                return documentsCount > 0;
            }
        };
        exportCSVLink.setOutputMarkupId(true);
        add(exportCSVLink);

        setSize(DialogConstants.SMALL_AUTO);
        setOkVisible(false);
        setFocusOnCancel();
    }

    /**
     * Defines the fields included in the CSV export. Override this method to add, remove, or reorder fields.
     * Each field has a header label and an extractor that receives the document variant node and its state summary.
     */
    protected List<CsvField> buildFields() {
        return Arrays.asList(
            new CsvField("title",                  (v, s) -> title(v)),
            new CsvField("url",                    (v, s) -> firstUrl(v)),
            new CsvField("content_owner",          (v, s) -> contactEmails(v, "publishing:contentOwner")),
            new CsvField("fact_checkers",          (v, s) -> contactEmails(v, "publishing:factCheckers")),
            new CsvField("review_date",            (v, s) -> reviewDateValue(v, REVIEW_DATE_PROP)),
            new CsvField("official_last_modified", (v, s) -> reviewDateValue(v, "publishing:lastUpdatedDate")),
            new CsvField("created_by",             (v, s) -> stringProperty(v, HIPPOSTDPUBWF_CREATED_BY)),
            new CsvField("date_modified", this::lastModifiedDate),
            new CsvField("modified_by", this::lastModifiedBy),
            new CsvField("id",                     (v, s) -> v.getIdentifier()),
            new CsvField("state",                  (v, s) -> stringProperty(v, HIPPOSTD_STATE)),
            new CsvField("life_events",            (v, s) -> multiValueProperty(v, "publishing:lifeEvents")),
            new CsvField("organisation_tags",      (v, s) -> multiValueProperty(v, "publishing:organisationtags")),
            new CsvField("format",                 (v, s) -> v.getPrimaryNodeType().getName()),
            new CsvField("mirrorTargetPublished",  (v, s) -> getMirrorTargetPublishedState(v.getPrimaryNodeType().getName(), v)),
            new CsvField("mirrorTarget",           (v, s) -> getMirrorTarget(v.getPrimaryNodeType().getName(), v))
        );
    }

    private ByteArrayResource getResource() {
        return new ByteArrayResource("text/csv") {
            @Override
            public byte[] getData(final Attributes attributes) {
                return createCsvData();
            }

            @Override
            protected void configureResponse(final ResourceResponse response, final Attributes attributes) {
                super.configureResponse(response, attributes);
                configureExportResponse(response);
            }
        };
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS));
    }

    private int getCountSelectedDocuments() {
        if (searcher != null) {
            int count = 0;
            Iterator<Node> documents = searcher.getSelectedDocuments();
            while (documents.hasNext()) {
                documents.next();
                count++;
            }
            return count;
        }
        return 0;
    }

    protected byte[] createCsvData() {
        List<CsvField> fields = buildFields();
        String[] headers = fields.stream().map(f -> f.header).toArray(String[]::new);
        StringWriter writer = new StringWriter();

        try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(headers).build())) {
            Iterator<Node> nodeIterator = searcher.getSelectedDocuments();
            while (nodeIterator.hasNext()) {
                Node handle = nodeIterator.next();
                List<String> values = extractValues(handle, fields);
                if (!values.isEmpty()) {
                    printer.printRecord(values);
                }
            }
        } catch (IOException e) {
            LOG.error("Error generating CSV export", e);
        }

        byte[] bytes = writer.toString().getBytes(StandardCharsets.UTF_8);
        return ArrayUtils.addAll(BOM, bytes);
    }

    private List<String> extractValues(Node handle, List<CsvField> fields) {
        String stateSummary = getNodeStateSummary(handle);
        Node variant = getVariant(handle, stateSummary);
        if (variant == null) {
            return Collections.emptyList();
        }

        List<String> values = new ArrayList<>();
        for (CsvField field : fields) {
            try {
                values.add(field.extractor.extract(variant, stateSummary));
            } catch (RepositoryException e) {
                LOG.error("Error extracting field '{}' for CSV export", field.header, e);
                values.add("");
            }
        }
        return values;
    }

    private Node getVariant(Node handle, String stateSummary) {
        return "live".equals(stateSummary) || "changed".equals(stateSummary)
                ? getDocumentVariantByHippoStdState(handle, PUBLISHED)
                : getDocumentVariantByHippoStdState(handle, UNPUBLISHED);
    }

    private String firstUrl(Node variant) {
        List<String> urls = getDocumentSiteURL(variant);
        return urls.isEmpty() ? "" : urls.get(0);
    }

    private String contactEmails(Node variant, String property) throws RepositoryException {
        if (!variant.hasNode(property)) {
            return "";
        }
        return extractContactEmails(variant.getNodes(property));
    }

    private String lastModifiedBy(Node variant, String stateSummary) throws RepositoryException {
        if ("changed".equals(stateSummary)) {
            variant = getDocumentVariantByHippoStdState(variant.getParent(), UNPUBLISHED);
        }
        return variant != null && variant.hasProperty(HIPPOSTDPUBWF_LAST_MODIFIED_BY)
                ? variant.getProperty(HIPPOSTDPUBWF_LAST_MODIFIED_BY).getString()
                : "";
    }

    private String lastModifiedDate(Node variant, String stateSummary) throws RepositoryException {
        if ("".equals(stateSummary)) {
            variant = getDocumentVariantByHippoStdState(variant.getParent(), UNPUBLISHED);
        }
        return variant != null && variant.hasProperty(HIPPOSTDPUBWF_LAST_MODIFIED_DATE)
                ? DateTimePrinter.of(variant.getProperty(HIPPOSTDPUBWF_LAST_MODIFIED_DATE).getDate()).print()
                : "";
    }

    private String stringProperty(Node variant, String property) throws RepositoryException {
        return variant.hasProperty(property) ? variant.getProperty(property).getString() : "";
    }

    String getMirrorTarget(String format, Node variant) throws RepositoryException {
        if (!"publishing:mirror".equals(format) || !variant.hasNode(PUBLISHING_DOCUMENT)) {
            return "";
        }
        String targetUuid = variant.getNode(PUBLISHING_DOCUMENT).getProperty(HIPPO_DOCBASE).getString();
        try {
            Node mirrorTarget = variant.getSession().getNodeByIdentifier(targetUuid);
            return mirrorTarget.getPath();
        } catch (ItemNotFoundException e) {
            LOG.warn("Mirror target not found in CSV export", e);
            return "";
        }
    }

    String getMirrorTargetPublishedState(String format, Node variant) throws RepositoryException {
        if (!"publishing:mirror".equals(format) || !variant.hasNode(PUBLISHING_DOCUMENT)) {
            return "";
        }
        String targetUuid = variant.getNode(PUBLISHING_DOCUMENT).getProperty(HIPPO_DOCBASE).getString();
        try {
            Node mirrorTarget = variant.getSession().getNodeByIdentifier(targetUuid);
            String stateSummary = getNodeStateSummary(mirrorTarget);
            Node mirrorTargetVariant = getVariant(mirrorTarget, stateSummary);
            if (mirrorTargetVariant == null) {
                return "false";
            }
            return Boolean.toString("published".equals(mirrorTargetVariant.getProperty(HIPPOSTD_STATE).getString()));
        } catch (ItemNotFoundException e) {
            LOG.warn("Mirror target not found in CSV export", e);
            return "";
        }
    }

    private String title(Node node) throws RepositoryException {
        if (node.hasProperty("publishing:title")) {
            return node.getProperty("publishing:title").getString();
        }
        Node handle = node.getParent();
        if (handle.hasProperty("hippo:name")) {
            return handle.getProperty("hippo:name").getString();
        }
        return handle.getName();
    }

    /**
     * The content team have asked that guide pages get the same review date as their parent guide in the CSV export.
     */
    private String reviewDateValue(Node variant, String property) throws RepositoryException {
        if (variant.isNodeType("publishing:guidepage")) {
            return guideReviewDateValue(variant, property);
        }
        if (!variant.hasProperty(property)) {
            return "";
        }
        return DateTimePrinter.of(variant.getProperty(property).getDate()).print();
    }

    private String guideReviewDateValue(Node variant, String property) throws RepositoryException {
        Node folder = variant.getParent().getParent();
        if (!folder.hasNode(INDEX)) {
            return "";
        }
        Node indexHandle = folder.getNode(INDEX);
        Node publishedVariant = getDocumentVariantByHippoStdState(indexHandle, PUBLISHED);
        Node unpublishedVariant = getDocumentVariantByHippoStdState(indexHandle, UNPUBLISHED);
        Node guideVariant = publishedVariant != null ? publishedVariant : unpublishedVariant;
        if (guideVariant != null) {
            return reviewDateValue(guideVariant, property);
        } else {
            return "";
        }
    }

    private String extractContactEmails(final NodeIterator nodeIterator) {
        List<String> contactEmails = new ArrayList<>();
        while (nodeIterator.hasNext()) {
            Node currentNode = nodeIterator.nextNode();
            try {
                if (currentNode.hasProperty(HIPPO_DOCBASE)) {
                    Session session = UserSession.get().getJcrSession();
                    String uuid = currentNode.getProperty(HIPPO_DOCBASE).getString();
                    Node contactHandle = HippoUtils.getNodeByIdentifierIfExists(session, uuid);
                    if (contactHandle != null) {
                        Node publishedVariant = getDocumentVariantByHippoStdState(contactHandle, PUBLISHED);
                        if (publishedVariant != null && publishedVariant.hasProperty("publishing:email")) {
                            contactEmails.add(publishedVariant.getProperty("publishing:email").getString());
                        }
                    }
                }
            } catch (RepositoryException e) {
                LOG.error("Error extracting contact email for CSV export", e);
            }
        }
        return String.join(", ", contactEmails);
    }

    private String multiValueProperty(Node variant, String property) throws RepositoryException {
        if (variant.isNodeType("publishing:guidepage")) {
            Node folder = variant.getParent().getParent();
            if (!folder.hasNode(INDEX)) {
                return "";
            }
            variant = getVariant(folder.getNode(INDEX), getNodeStateSummary(folder.getNode(INDEX)));
        }
        if (variant == null || !variant.hasProperty(property)) {
            return "";
        }
        Value[] values = variant.getProperty(property).getValues();
        String[] results = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i].getString();
        }
        return String.join(", ", results);
    }

    private String getNodeStateSummary(final Node handle) {
        try {
            Node randomVariant = handle.getNodes(handle.getName()).nextNode();
            if (randomVariant.hasProperty(HIPPOSTD_STATESUMMARY)) {
                return randomVariant.getProperty(HIPPOSTD_STATESUMMARY).getString();
            }
        } catch (RepositoryException e) {
            LOG.error("Error retrieving variant state summary", e);
        }
        return null;
    }

    private Node getDocumentVariantByHippoStdState(final Node handle, final String hippoStdState) {
        try {
            for (NodeIterator nodeIt = handle.getNodes(handle.getName()); nodeIt.hasNext(); ) {
                Node variantNode = nodeIt.nextNode();
                if (variantNode.hasProperty(HippoStdNodeType.HIPPOSTD_STATE)) {
                    String state = variantNode.getProperty(HippoStdNodeType.HIPPOSTD_STATE).getString();
                    if (StringUtils.equals(hippoStdState, state)) {
                        return variantNode;
                    }
                }
            }
        } catch (RepositoryException e) {
            LOG.error("Error retrieving {} variant of a node", hippoStdState, e);
        }
        return null;
    }

    private List<String> getDocumentSiteURL(Node node) {
        HstRequestContext hstRequestContext = RequestContextProvider.get();
        HstModelRegistry hstModelRegistry = HippoServiceRegistry.getService(HstModelRegistry.class);
        final HstModel siteModel = hstModelRegistry.getHstModel("/site");
        final HstLinkCreator hstLinkCreator = siteModel.getHstLinkCreator();
        return siteModel.getVirtualHosts().getMountsByHostGroup(HstUtil.getHostGroup())
                .stream()
                .filter(mount -> {
                    try {
                        return !"preview".equals(mount.getType()) && node.getPath().contains(mount.getContentPath());
                    } catch (RepositoryException e) {
                        LOG.error("Error during URL generation in CSV export", e);
                    }
                    return false;
                })
                .map(mount -> hstLinkCreator.create(node, mount).toUrlForm(hstRequestContext, true))
                .collect(Collectors.toList());
    }

    protected void configureExportResponse(final AbstractResource.ResourceResponse response) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String filename = "export-" + LocalDate.now().format(dtf) + ".csv";
        response.setFileName(filename);
        response.setContentDisposition(ContentDisposition.ATTACHMENT);
        response.disableCaching();
    }
}
