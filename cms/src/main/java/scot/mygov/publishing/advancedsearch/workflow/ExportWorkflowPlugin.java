package scot.mygov.publishing.advancedsearch.workflow;

import org.apache.wicket.model.Model;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.hippoecm.repository.api.Workflow;

import com.onehippo.cms7.search.frontend.ISearchContext;

public class ExportWorkflowPlugin extends RenderPlugin {

    public ExportWorkflowPlugin(final IPluginContext context,
                                final IPluginConfig config) {
        super(context, config);

        WorkflowDescriptorModel model = (WorkflowDescriptorModel) getModel();
        final ISearchContext searcher = context.getService(
                ISearchContext.class.getName(), ISearchContext.class);

        add(new StdWorkflow
                ("csvexport", Model.of("Export CSV"), context, model) {

            @Override
            public String getSubMenu() {
                return "top";
            }

            @Override
            protected String execute(Workflow workflow) throws Exception {
                return null;
            }

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                return new ExportDialog(searcher);
            }
        });
    }
}