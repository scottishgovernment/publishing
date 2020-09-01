package scot.mygov.publishing.plugins;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.dialog.ExceptionDialog;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scot.mygov.publishing.dialogs.PreviewDatePickerDialog;
import static org.hippoecm.repository.HippoStdNodeType.NT_DIRECTORY;
import static org.hippoecm.repository.HippoStdNodeType.NT_FOLDER;
import static org.hippoecm.repository.api.HippoNodeType.NT_HANDLE;

public class PreviewFolderMenuItemPlugin extends RenderPlugin {

    private static final long serialVersionUID = 1L;
    private static Logger log = LoggerFactory.getLogger(PreviewFolderMenuItemPlugin.class);

    public PreviewFolderMenuItemPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);

        add(new StdWorkflow("menuItem", getMenuItemLabelModel(), getMenuItemIconResourceReference(), context, (WorkflowDescriptorModel) getModel()) {

            private static final long serialVersionUID = 1L;

            @Override
            protected IDialogService.Dialog createRequestDialog() {
                try {
                    Set<String> nodeIDs = new HashSet<>();
                    folderPreviewGeneration(getModel().getNode(), nodeIDs);
                    return new PreviewDatePickerDialog(nodeIDs, getInvalidationTime());
                } catch (RepositoryException e){
                    log.error("An exception occurred while trying to generate a preview link for a folder.");
                    return new ExceptionDialog(e);
                }
            }

        });
    }

    private void folderPreviewGeneration(final Node folder, final Set<String> nodeIDs) throws RepositoryException {
        for (Node child : new NodeIterable(folder.getNodes())) {
            if (child.isNodeType(NT_FOLDER) || child.isNodeType(NT_DIRECTORY)) {
                folderPreviewGeneration(child, nodeIDs);
            } else if (child.isNodeType(NT_HANDLE)) {
                nodeIDs.add(child.getIdentifier());
            }
        }
    }

    private long getInvalidationTime(){
        try{
            Node node = UserSession.get().getJcrSession().getNode("/hippo:configuration/hippo:workflows/default/handle/frontend:renderer/preview");
            return JcrUtils.getLongProperty(node, "invalidation.time.days", 0L);
        } catch (RepositoryException e) {
            log.error("An exception occurred while trying to read invalidation time property for preview sharing plugin.", e);
        }
        return 0;
    }

    protected IModel<String> getDialogTitleModel() {
        return new StringResourceModel("folder.action.preview.dialog.label", this, null);
    }

    protected IModel<String> getMenuItemLabelModel() {
        return new StringResourceModel("folder.action.preview.menuitem.label", this, null);
    }

    private ResourceReference getMenuItemIconResourceReference() {
        return new PackageResourceReference(getClass(), "preview-icon-16.png");
    }

}
