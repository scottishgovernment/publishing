package scot.mygov.publishing.advancedsearch.filters;

import com.onehippo.cms7.search.frontend.ISearchContext;
import com.onehippo.cms7.search.frontend.constraints.IConstraintProvider;
import com.onehippo.cms7.search.frontend.filters.GenericFiltersPlugin;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.wicketstuff.datetime.PatternDateConverter;
import org.wicketstuff.datetime.markup.html.form.DateTextField;
import org.wicketstuff.datetime.extensions.yui.calendar.DatePicker;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.hippoecm.frontend.behaviors.EventStoppingDecorator;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.onehippo.cms7.services.search.query.QueryUtils;
import org.onehippo.cms7.services.search.query.constraint.Constraint;
import org.onehippo.cms7.services.search.query.constraint.LowerBoundedDateConstraint;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CustomFiltersPlugin extends RenderPlugin implements IConstraintProvider {

    private static final ResourceReference DATE_PICKER_ICON = new PackageResourceReference(GenericFiltersPlugin.class, "images/calendar.png");
    private static final String RESET_CONTAINER = "-reset-container";
    private String datePattern = "dd/MM/yyyy";

    private Date reviewDateFrom;
    private Date reviewDateTo;
    private Date lastOfficialUpdateDateFrom;
    private Date lastOfficialUpdateDateTo;
    private boolean pendingPublication = false;

    public CustomFiltersPlugin(final IPluginContext context, final IPluginConfig config) {
        super(context, config);
        Form form = new Form("form", new CompoundPropertyModel(this));

        final DateTextField reviewDateAfter = new DateTextField("review-date-from",
                new PropertyModel<>(this, "reviewDateFrom"),
                new PatternDateConverter(getDatePattern(), true)) {

            @Override
            protected void onModelChanged() {
                super.onModelChanged();
                updateSearchResults();
            }
        };
        reviewDateAfter.add(createDatePicker());
        reviewDateAfter.add(createSimpleAjaxChangeBehavior());
        form.add(reviewDateAfter);
        form.add(createSimpleResetLink(reviewDateAfter));

        final DateTextField reviewDateBefore = new DateTextField("review-date-to",
                new PropertyModel<>(this, "reviewDateTo"),
                new PatternDateConverter(getDatePattern(), true)) {

            @Override
            protected void onModelChanged() {
                super.onModelChanged();
                updateSearchResults();
            }
        };
        reviewDateBefore.add(createDatePicker());
        reviewDateBefore.add(createSimpleAjaxChangeBehavior());
        form.add(reviewDateBefore);
        form.add(createSimpleResetLink(reviewDateBefore));

        final DateTextField lastOfficialUpdateDateAfter = new DateTextField("last-official-update-date-from",
                new PropertyModel<>(this, "lastOfficialUpdateDateFrom"),
                new PatternDateConverter(getDatePattern(), true)) {

            @Override
            protected void onModelChanged() {
                super.onModelChanged();
                updateSearchResults();
            }
        };
        lastOfficialUpdateDateAfter.add(createDatePicker());
        lastOfficialUpdateDateAfter.add(createSimpleAjaxChangeBehavior());
        form.add(lastOfficialUpdateDateAfter);
        form.add(createSimpleResetLink(lastOfficialUpdateDateAfter));

        final DateTextField lastOfficialUpdateDateBefore = new DateTextField("last-official-update-date-to",
                new PropertyModel<>(this, "lastOfficialUpdateDateTo"),
                new PatternDateConverter(getDatePattern(), true)) {

            @Override
            protected void onModelChanged() {
                super.onModelChanged();
                updateSearchResults();
            }
        };
        lastOfficialUpdateDateBefore.add(createDatePicker());
        lastOfficialUpdateDateBefore.add(createSimpleAjaxChangeBehavior());
        form.add(lastOfficialUpdateDateBefore);
        form.add(createSimpleResetLink(lastOfficialUpdateDateBefore));

        form.add(new CheckBox("pendingPublication",new PropertyModel<Boolean>(this, "pendingPublication")){

            @Override
            protected void onModelChanged() {
                super.onModelChanged();
                updateSearchResults();
            }
        });

        add(form);
    }

    private void updateSearchResults() {
        ISearchContext searcher = getPluginContext().getService(
                ISearchContext.class.getName(),
                ISearchContext.class);
        searcher.updateSearchResults();
        final AjaxRequestTarget target = getRequestCycle().find(AjaxRequestTarget.class).get();
        visitChildren(MarkupContainer.class, new IVisitor<MarkupContainer, Void>() {
            @Override
            public void component(final MarkupContainer container, IVisit<Void> visit) {
                if (container.getId().endsWith(RESET_CONTAINER)) {
                    target.add(container);
                    visit.dontGoDeeper();
                }
            }
        });
    }

    @Override
    public List<Constraint> getConstraints() {
        List<Constraint> constraints = new LinkedList<Constraint>();
        addDateConstraints(constraints, "publishing:reviewDate", reviewDateFrom, reviewDateTo);
        addDateConstraints(constraints, "publishing:lastUpdatedDate", lastOfficialUpdateDateFrom, lastOfficialUpdateDateTo);
        if(pendingPublication) {
            constraints.add(QueryUtils.text("../*/@jcr:primaryType").isEqualTo("hippostdpubwf:request"));
            constraints.add(QueryUtils.not(QueryUtils.text("../*/*/@hippostdpubwf:type").isEqualTo("rejected")));
        }
        return constraints;
    }

    private void addDateConstraints(final List<Constraint> constraints,
                                    final String property,
                                    final Date dateAfter,
                                    final Date dateBefore) {
        if (dateAfter != null) {
            final LowerBoundedDateConstraint constraint = QueryUtils.date(property).from(dateAfter);
            constraints.add(constraint);
            if (dateBefore != null) {
                constraint.andTo(dateBefore);
            }
        } else if (dateBefore != null) {
            constraints.add(QueryUtils.date(property).to(dateBefore));
        }
    }

    @Override
    public void clearConstraints() {
        reviewDateFrom = null;
        reviewDateTo = null;
        lastOfficialUpdateDateFrom = null;
        lastOfficialUpdateDateTo = null;
        pendingPublication = false;
        redraw();
    }

    protected String getDatePattern() {
        return datePattern;
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

    private MarkupContainer createSimpleResetLink(final Component component) {
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
                component.setDefaultModelObject(null);
                target.add(component);
            }
        });
        container.add(resetImage);
        return container;
    }
}
