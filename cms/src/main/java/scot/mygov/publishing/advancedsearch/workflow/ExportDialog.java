package scot.mygov.publishing.advancedsearch.workflow;

import com.google.common.base.Charsets;
import com.onehippo.cms7.search.frontend.ISearchContext;
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

import javax.jcr.*;
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
    private static final String DEFAULT_LINE_TERMINATOR = "\r\n";
    private static final String QUOTE = "\"";
    private static final String DEFAULT_SEPARATOR = ",";
    private static final String SEPARATOR_WITH_QUOTES = QUOTE + DEFAULT_SEPARATOR + QUOTE;

    private static final String REVIEW_DATE_PROP = "publishing:reviewDate";

    //When required to add a new property in the CSV export add displayed label and corresponding property in the two lists below.
    //You might have to change #constructPropertiesList method below to add your property if it requires special handling.

    private static final String[] headers = new String[]{"title", "url", "content_owner", "fact_checkers", "review_date", "official_last_modified", "created_by", "date_modified", "modified_by", "id", "state", "life_events", "organisation_tags", "format" };
    private static final String[] documentProperties = new String[]{"title", "url", "publishing:contentOwner", "publishing:factCheckers", REVIEW_DATE_PROP, "publishing:lastUpdatedDate", HIPPOSTDPUBWF_CREATED_BY, HIPPOSTDPUBWF_LAST_MODIFIED_DATE, HIPPOSTDPUBWF_LAST_MODIFIED_BY, "id", HIPPOSTD_STATE, "publishing:lifeEvents", "publishing:organisationtags", "format" };

    private final ResourceLink<String> exportCSVLink;
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

        exportCSVLink = new ResourceLink<String>("exportCSV", getResource()) {

            @Override
            public boolean isEnabled(){
                return documentsCount > 0;
            }

        };
        exportCSVLink.setOutputMarkupId(true);
        add(exportCSVLink);

        setSize(DialogConstants.SMALL_AUTO);
        setOkVisible(false);
        setFocusOnCancel();
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

    private int getCountSelectedDocuments(){
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
        /// ugh, we should use a csv library for this...
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(QUOTE);
        stringBuilder.append(StringUtils.join(headers, SEPARATOR_WITH_QUOTES));
        stringBuilder.append(QUOTE);
        stringBuilder.append(DEFAULT_LINE_TERMINATOR);

        Iterator<Node> nodeIterator = searcher.getSelectedDocuments();
        while (nodeIterator.hasNext()) {
            Node currentNode = nodeIterator.next();
            List<String> propertiesList = constructPropertiesList(currentNode);
            for (int i = 0; i < propertiesList.size(); i++) {
                appendValue(stringBuilder, i, propertiesList.get(i));
            }
            if(!propertiesList.isEmpty()){
                stringBuilder.append(DEFAULT_LINE_TERMINATOR);
            }
        }

        final byte[] bytes = stringBuilder.toString().getBytes(Charsets.UTF_8);
        return ArrayUtils.addAll(BOM, bytes);
    }

    private List<String> constructPropertiesList(final Node handle) {
        List<String> props = new ArrayList<>();
        String stateSummary = getNodeStateSummary(handle);
        Node variant = getVariant(handle, stateSummary);
        if (variant == null) {
            return Collections.emptyList();
        }

        for (String property : documentProperties) {
            try {
                addProperty(variant, property, stateSummary, props);
            } catch (RepositoryException e){
                LOG.error("An exception occurred while exporting CSV for the property {} ", property, e);
                props.add("---Exception occurred---" + e.getMessage());
            }
        }


        return props;
    }

    private Node getVariant(Node handle, String stateSummary) {
        return "live".equals(stateSummary) || "changed".equals(stateSummary)
                ? getDocumentVariantByHippoStdState(handle, PUBLISHED)
                : getDocumentVariantByHippoStdState(handle, UNPUBLISHED);
    }

    private void addProperty(Node variant, String property, String stateSummary, List<String> props) throws RepositoryException {
        switch (property) {
            case "title":
                props.add(useFirstPresentProperty(variant, "publishing:title", "hippo:name"));
                break;
            case "url":
                List<String> urls = getDocumentSiteURL(variant);
                if (!urls.isEmpty()) {
                    props.add(urls.get(0));
                } else {
                    props.add("");
                }
                break;
            case "id":
                props.add(variant.getIdentifier());
                break;
            case "format":
                props.add(variant.getPrimaryNodeType().getName());
                break;
            case HIPPOSTDPUBWF_LAST_MODIFIED_BY:
                if ("changed".equals(stateSummary)) {
                    variant = getDocumentVariantByHippoStdState(variant.getParent(), UNPUBLISHED);
                }
                if (variant != null && variant.hasProperty(HIPPOSTDPUBWF_LAST_MODIFIED_BY)) {
                    props.add(variant.getProperty(HIPPOSTDPUBWF_LAST_MODIFIED_BY).getString());
                } else {
                    props.add("");
                }
                break;
            case HIPPOSTDPUBWF_LAST_MODIFIED_DATE:
                if ("".equals(stateSummary)) {
                    variant = getDocumentVariantByHippoStdState(variant.getParent(), UNPUBLISHED);
                }
                if (variant != null && variant.hasProperty(HIPPOSTDPUBWF_LAST_MODIFIED_DATE)) {
                    props.add(DateTimePrinter.of(variant.getProperty(HIPPOSTDPUBWF_LAST_MODIFIED_DATE).getDate()).print());
                } else {
                    props.add("");
                }
                break;
            case "publishing:contentOwner":
            case "publishing:factCheckers":
                if (variant.hasNode(property)) {
                    props.add(extractContactEmails(variant.getNodes(property)));
                } else {
                    props.add("");
                }
                break;
            case "publishing:lifeEvents":
                props.add(multiValueProperty(variant, property));
                break;
            case "publishing:organisationtags":
                props.add(multiValueProperty(variant, property));
                break;
            case REVIEW_DATE_PROP :
            case "publishing:lastUpdatedDate":
                reviewDate(variant, property, props);
                break;

            default:
                if (variant.hasProperty(property)) {
                    props.add(variant.getProperty(property).getString());
                } else {
                    props.add("");
                }
                break;
        }
    }

    private String useFirstPresentProperty(Node node, String ... props) throws RepositoryException {
        for (String prop : props) {
            if (node.hasProperty(prop) && StringUtils.isNotBlank(node.getProperty(prop).getString())) {
                return node.getProperty(prop).getString();
            }
        }
        return node.getName();
    }

    /**
     * the content team have asked that guiod pages get the same rview date as their parent guid in the csv export
     */
    private void reviewDate(Node variant, String property, List<String> props) throws RepositoryException {

        if (variant.isNodeType("publishing:guidepage")) {
            guideReviewDate(variant, property, props);
            return;
        }
        Calendar reviewDate = variant.hasProperty(property)
                    ? variant.getProperty(property).getDate()
                    : null;

        if (reviewDate != null) {
            props.add(DateTimePrinter.of(reviewDate).print());
        } else {
            props.add("");
        }
    }

    void guideReviewDate(Node variant, String property, List<String> props) throws RepositoryException {
        Node folder = variant.getParent().getParent();
        Node indexHandle = folder.getNode("index");
        Node publishedVariant = getDocumentVariantByHippoStdState(indexHandle, PUBLISHED);
        Node unpublishedVariant = getDocumentVariantByHippoStdState(indexHandle, UNPUBLISHED);
        Node guideVariant = publishedVariant != null
                ? publishedVariant : unpublishedVariant;
        reviewDate(guideVariant, property, props);
    }

    private String extractContactEmails(final NodeIterator nodeIterator){
        ArrayList<String> contactEmails = new ArrayList<>();
        while (nodeIterator.hasNext()){
            Node currentNode = nodeIterator.nextNode();
            try {
                if (currentNode.hasProperty(HIPPO_DOCBASE)) {
                    Node contactHandle = UserSession.get().getJcrSession().getNodeByIdentifier(currentNode.getProperty(HIPPO_DOCBASE).getString());
                    Node publishedVariant = getDocumentVariantByHippoStdState(contactHandle, PUBLISHED);
                    if (publishedVariant != null && publishedVariant.hasProperty("publishing:email")) {
                        contactEmails.add(publishedVariant.getProperty("publishing:email").getString());
                    }
                }
            } catch (RepositoryException e){
                LOG.error("An exception occurred while trying to extract contact emails for one of the selected documents.", e);
            }
        }
        return String.join(", ", contactEmails);
    }

    private String multiValueProperty(Node variant, String property) throws RepositoryException {
        if (variant.isNodeType("publishing:guidepage")) {
            Node guideHandle = variant.getParent().getParent().getNode("index");
            variant = getVariant(guideHandle, getNodeStateSummary(guideHandle));
        }

        if (!variant.hasProperty(property)) {
            return "";
        }

        return extractMultiValueProperty(variant.getProperty(property));
    }

    private String extractMultiValueProperty(final Property property){
        String valueString = null;

        try {
            Value[] values = property.getValues();
            String[] results = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                results[i] = values[i].getString();
            }
            valueString = String.join(", ", results);
        } catch (RepositoryException e) {
            LOG.error("An exception occurred while trying to extract multi valued property {}", e);
        }

        return valueString;
    }

    private String getNodeStateSummary(final Node handle) {
        try {
            Node randomVariant = handle.getNodes(handle.getName()).nextNode();
            if (randomVariant.hasProperty(HIPPOSTD_STATESUMMARY)) {
                return randomVariant.getProperty(HIPPOSTD_STATESUMMARY).getString();
            }
        } catch (RepositoryException e){
            LOG.error("An exception occurred while trying to retrieve variant state summary.", e);
        }
        return null;
    }

    private Node getDocumentVariantByHippoStdState(final Node handle, final String hippoStdState) {
        Node variantNode = null;
        String state;

        try {
            for (NodeIterator nodeIt = handle.getNodes(handle.getName()); nodeIt.hasNext(); ) {
                variantNode = nodeIt.nextNode();

                if (variantNode.hasProperty(HippoStdNodeType.HIPPOSTD_STATE)) {
                    state = variantNode.getProperty(HippoStdNodeType.HIPPOSTD_STATE).getString();
                    if (StringUtils.equals(hippoStdState, state)) {
                        return variantNode;
                    }
                }
            }
        } catch (RepositoryException e){
            LOG.error("An exception occurred while trying to retrieve the {} variant of a node.", hippoStdState, e);
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
                        LOG.error("An exception occurred during preview link creation.", e);
                    }
                    return false;
                })
                .map(mount -> hstLinkCreator.create(node, mount).toUrlForm(hstRequestContext, true))
                .collect(Collectors.toList());
    }

    private void appendValue(final StringBuilder stringBuilder, final int position, final String value){
        if (position == 0) {
            stringBuilder.append(QUOTE);
            stringBuilder.append(value);
            stringBuilder.append(QUOTE);
        } else {
            stringBuilder.append(DEFAULT_SEPARATOR);
            stringBuilder.append(QUOTE);
            stringBuilder.append(value);
            stringBuilder.append(QUOTE);
        }
    }

    protected void configureExportResponse(final AbstractResource.ResourceResponse response) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String filename = "export-"+LocalDate.now().format(dtf)+".csv";
        response.setFileName(filename);
        response.setContentDisposition(ContentDisposition.ATTACHMENT);
        response.disableCaching();
    }

}
