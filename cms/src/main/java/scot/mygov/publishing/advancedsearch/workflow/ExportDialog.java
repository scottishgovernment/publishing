package scot.mygov.publishing.advancedsearch.workflow;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

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

import com.google.common.base.Charsets;
import com.onehippo.cms7.search.frontend.ISearchContext;

import static org.hippoecm.repository.HippoStdNodeType.HIPPOSTD_STATE;
import static org.hippoecm.repository.HippoStdNodeType.HIPPOSTD_STATESUMMARY;
import static org.hippoecm.repository.HippoStdNodeType.PUBLISHED;
import static org.hippoecm.repository.HippoStdNodeType.UNPUBLISHED;
import static org.hippoecm.repository.HippoStdPubWfNodeType.HIPPOSTDPUBWF_CREATED_BY;
import static org.hippoecm.repository.HippoStdPubWfNodeType.HIPPOSTDPUBWF_LAST_MODIFIED_BY;
import static org.hippoecm.repository.HippoStdPubWfNodeType.HIPPOSTDPUBWF_LAST_MODIFIED_DATE;
import static org.hippoecm.repository.api.HippoNodeType.HIPPO_DOCBASE;

public class ExportDialog extends Dialog<WorkflowDescriptor> {

    private static final Logger LOG = LoggerFactory.getLogger(ExportDialog.class);
    private static final CssResourceReference CSS = new CssResourceReference(ExportDialog.class, "ExportDialog.css");

    private static final byte[] BOM = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private static final String DEFAULT_LINE_TERMINATOR = "\r\n";
    private static final String QUOTE = "\"";
    private static final String DEFAULT_SEPARATOR = ",";
    private static final String separatorWithQuotes = QUOTE + DEFAULT_SEPARATOR + QUOTE;

    //When required to add a new property in the CSV export add displayed label and corresponding property in the two lists below.
    //You might have to change #constructPropertiesList method below to add your property if it requires special handling.
    private static final String[] headers = new String[]{"fact_checkers", "format" , "content_owner", "title", "created_by", "url", "review_date", "date_modified", "modified_by", "id", "state"};
    private static final String[] documentProperties = new String[]{"publishing:factCheckers", "format" , "publishing:contentOwner", "hippo:name", HIPPOSTDPUBWF_CREATED_BY, "url", "publishing:reviewDate", HIPPOSTDPUBWF_LAST_MODIFIED_DATE, HIPPOSTDPUBWF_LAST_MODIFIED_BY, "id", HIPPOSTD_STATE};

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
        final StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(QUOTE);
        stringBuilder.append(StringUtils.join(headers, separatorWithQuotes));
        stringBuilder.append(QUOTE);
        stringBuilder.append(DEFAULT_LINE_TERMINATOR);

        Iterator<Node> nodeIterator = searcher.getSelectedDocuments();
        while (nodeIterator.hasNext()) {
            Node currentNode = nodeIterator.next();
            List<String> propertiesList = constructPropertiesList(currentNode);
            for (int i = 0; i < propertiesList.size(); i++) {
                appendValue(stringBuilder, i, propertiesList.get(i));
            }
            if(propertiesList.size()>0){
                stringBuilder.append(DEFAULT_LINE_TERMINATOR);
            }
        }

        final byte[] bytes = stringBuilder.toString().getBytes(Charsets.UTF_8);
        return ArrayUtils.addAll(BOM, bytes);
    }

    private List<String> constructPropertiesList(final Node handle) {
        ArrayList<String> props = new ArrayList<>();
        String nodeStateSummary = getNodeStateSummary(handle);
        Node variant;

        for (String property : documentProperties) {
            if ("live".equals(nodeStateSummary) || "changed".equals(nodeStateSummary)) {
                variant = getDocumentVariantByHippoStdState(handle, PUBLISHED);
            } else {
                variant = getDocumentVariantByHippoStdState(handle, UNPUBLISHED);
            }
            if (variant != null) {
                try {
                    switch (property) {
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
                        case "publishing:reviewDate":
                            if (variant.hasProperty(property)) {
                                props.add(DateTimePrinter.of(variant.getProperty("publishing:reviewDate").getDate()).print());
                            } else {
                                props.add("");
                            }
                            break;
                        case HIPPOSTDPUBWF_LAST_MODIFIED_BY:
                            if ("changed".equals(nodeStateSummary)) {
                                variant = getDocumentVariantByHippoStdState(handle, UNPUBLISHED);
                            }
                            if (variant != null && variant.hasProperty(HIPPOSTDPUBWF_LAST_MODIFIED_BY)) {
                                props.add(variant.getProperty(HIPPOSTDPUBWF_LAST_MODIFIED_BY).getString());
                            } else {
                                props.add("");
                            }
                            break;
                        case HIPPOSTDPUBWF_LAST_MODIFIED_DATE:
                            if ("changed".equals(nodeStateSummary)) {
                                variant = getDocumentVariantByHippoStdState(handle, UNPUBLISHED);
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
                        default:
                            if (variant.hasProperty(property)) {
                                props.add(variant.getProperty(property).getString());
                            } else {
                                props.add("");
                            }
                            break;
                    }
                } catch (RepositoryException e){
                    LOG.error("An exception occurred while exporting CSV for the property {} ", property, e);
                    props.add("---Exception occurred---");
                }
            }
        }

        return props;
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
                LOG.error("An exception occurred while trying to extra contact emails for one of the selected documents.", e);
            }
        }
        return String.join(", ", contactEmails);
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
