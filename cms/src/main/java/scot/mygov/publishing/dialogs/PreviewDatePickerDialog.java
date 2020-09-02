package scot.mygov.publishing.dialogs;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.http.client.utils.URIBuilder;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hippoecm.frontend.behaviors.EventStoppingDecorator;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.platform.model.HstModel;
import org.hippoecm.hst.platform.model.HstModelRegistry;
import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.onehippo.cms7.channelmanager.HstUtil;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.onehippo.cms7.search.frontend.constraints.IConstraintProvider;
import com.onehippo.cms7.search.frontend.filters.GenericFiltersPlugin;

import static scot.mygov.publishing.plugins.PreviewPlugin.INTERNAL_PREVIEW_NODE_NAME;

public class PreviewDatePickerDialog extends AbstractDialog {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(PreviewDatePickerDialog.class);
    private static final String RESET_CONTAINER = "-reset-container";
    private static final CssResourceReference CSS = new CssResourceReference(PreviewDatePickerDialog.class, "PreviewDatePickerDialog.css");
    private static final ResourceReference DATE_PICKER_ICON = new PackageResourceReference(GenericFiltersPlugin.class, "images/calendar.png");
    private String title;
    private final long invalidationTime;
    private Date expirationDate;
    private Set<String> nodeIDs;

    public PreviewDatePickerDialog(final Set<String> nodeIDs, final long invalidationTime) {
        this.title = getString("dialog-title", null, "Preview generation");
        this.nodeIDs = nodeIDs;
        this.invalidationTime = invalidationTime;

        Form form = new Form<>("form", new CompoundPropertyModel<>(this));
        final DateTextField publicationDateBefore = new DateTextField("expiration-date",
                new PropertyModel<>(this, "expirationDate"),
                new PatternDateConverter("dd/MM/yyyy", true)) {

            @Override
            protected void onModelChanged() {
                super.onModelChanged();
                final AjaxRequestTarget target = getRequestCycle().find(AjaxRequestTarget.class);
                if (target != null) {
                    target.add(this.getParent().get(this.getId() + RESET_CONTAINER));
                }
            }
        };
        publicationDateBefore.add(createDatePicker());
        publicationDateBefore.add(createSimpleAjaxChangeBehavior());
        form.add(publicationDateBefore);
        form.add(createSimpleResetLink(publicationDateBefore, this));
        add(form);

        Label counttext = new Label("counttext", MessageFormat.format(
                new StringResourceModel("publish-all-subtext",
                        PreviewDatePickerDialog.this).getString(), nodeIDs.size()
        ));
        add(counttext);
        counttext.setVisible(nodeIDs.size()>1);

        setOutputMarkupId(true);
        setOkEnabled(false);
        setCancelVisible(true);
    }


    private DatePicker createDatePicker() {
        DatePicker datePicker = new DatePicker() {
            @Override
            protected String getAdditionalJavaScript() {
                return "${calendar}.cfg.setProperty(\"navigator\",true,false); ${calendar}.render();";
            }

            @Override
            protected boolean alignWithIcon() {
                return false;
            }

            @Override
            protected CharSequence getIconUrl() {
                return RequestCycle.get().urlFor(new ResourceReferenceRequestHandler(DATE_PICKER_ICON));
            }
        };
        datePicker.setShowOnFieldClick(true);
        return datePicker;
    }

    private AjaxFormComponentUpdatingBehavior createSimpleAjaxChangeBehavior(final Component... components) {
        return new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                setOkEnabled(true);
                if (components != null) {
                    for (final Component component : components) {
                        target.add(component);
                        Component reset = component.getParent().get(component.getId() + RESET_CONTAINER);
                        if (reset != null) {
                            target.add(reset);
                        }
                    }
                }
            }
        };
    }

    private MarkupContainer createSimpleResetLink(final Component component, final PreviewDatePickerDialog dialog) {
        WebMarkupContainer container = new WebMarkupContainer(component.getId() + RESET_CONTAINER);
        container.setOutputMarkupId(true);
        Image resetImage = new Image(component.getId() + "-reset", IConstraintProvider.RESET_ICON) {

            @Override
            public boolean isVisible() {
                return component.getDefaultModelObject() != null;
            }
        };
        resetImage.add(new AjaxEventBehavior("click") {

            @Override
            protected void updateAjaxAttributes(final AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.getAjaxCallListeners().add(new EventStoppingDecorator());
            }

            @Override
            protected void onEvent(final AjaxRequestTarget target) {
                dialog.setOkEnabled(false);
                component.setDefaultModelObject(null);
                target.add(component);
            }
        });
        container.add(resetImage);
        return container;
    }

    @Override
    protected void onOk() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        Session session = UserSession.get().getJcrSession();
        nodeIDs.forEach(id->{
            try {
                Node node = session.getNodeByIdentifier(id);
                List<String> urls = getPreviewURLs(node);
                generatePreviewLinkNodes(node, urls, randomUUIDString);
            } catch (RepositoryException e){
                log.error("An exception occurred while generating a preview for node with uuid {}", id, e);
            }
        });
    }

    private List<String> getPreviewURLs(Node node) {
        HstRequestContext hstRequestContext = RequestContextProvider.get();
        HstModelRegistry hstModelRegistry = HippoServiceRegistry.getService(HstModelRegistry.class);
        final HstModel siteModel = hstModelRegistry.getHstModel("/site");
        final HstLinkCreator hstLinkCreator = siteModel.getHstLinkCreator();
        return siteModel.getVirtualHosts().getMountsByHostGroup(HstUtil.getHostGroup())
                .stream()
                .filter(mount -> {
                    try {
                        return "preview".equals(mount.getType()) && mount.getAlias().endsWith("-staging") && node.getPath().contains(mount.getContentPath());
                    } catch (RepositoryException e) {
                        log.error("An exception occurred during preview link creation.", e);
                    }
                    return false;
                })
                .map(mount -> hstLinkCreator.create(node, mount).toUrlForm(hstRequestContext, true))
                .collect(Collectors.toList());
    }

    protected void generatePreviewLinkNodes(final Node node, final List<String> urls, final String randomUUIDString) throws RepositoryException {
        Node unpublishedVariant = getUnpublishedVariant(node);
        urls.stream().forEach(url -> {
            if (unpublishedVariant != null) {
                //storing the preview id in a separate child node
                try {
                    String fullURL = new URIBuilder(url).addParameter("previewkey", randomUUIDString).toString();
                    Node previewId = unpublishedVariant.addNode(INTERNAL_PREVIEW_NODE_NAME, "staging:preview");
                    previewId.setProperty("staging:key", randomUUIDString);
                    previewId.setProperty("staging:url", fullURL);
                    previewId.setProperty("staging:user", UserSession.get().getJcrSession().getUser().getId());
                    Calendar creationCalendar = Calendar.getInstance();
                    previewId.setProperty("staging:creationdate", creationCalendar);
                    Calendar expiration = determineExpiration(expirationDate, creationCalendar);
                    if(expiration!=null) {
                        previewId.setProperty("staging:expirationdate", expiration);
                    }
                    previewId.getSession().save();
                } catch (RepositoryException e) {
                    log.error("Exception while generating preview link nodes.", e);
                } catch (URISyntaxException e){
                    log.error("Exception while constructing full URL.", e);
                }
            }
        });
    }

    private Calendar determineExpiration(final Date expirationDate, final Calendar creationCalendar){
        if(invalidationTime <= 0){
            if(expirationDate == null){
                return null;
            } else {
                Calendar expiration = Calendar.getInstance();
                expiration.setTime(expirationDate);
                return expiration;
            }
        } else {
            if(expirationDate == null){
                creationCalendar.add(Calendar.DATE, Math.toIntExact(invalidationTime));
                return creationCalendar;
            } else {
                Calendar expiration = Calendar.getInstance();
                expiration.setTime(expirationDate);
                return expiration;
            }
        }
    }

    private static Node getUnpublishedVariant(Node handle) throws RepositoryException {
        for (Node variant : new NodeIterable(handle.getNodes(handle.getName()))) {
            final String state = JcrUtils.getStringProperty(variant, HippoStdNodeType.HIPPOSTD_STATE, null);
            if (HippoStdNodeType.UNPUBLISHED.equals(state)) {
                return variant;
            }
        }
        return null;
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(CSS));
    }

    @Override
    public IModel getTitle() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return title;
            }
        };
    }
}