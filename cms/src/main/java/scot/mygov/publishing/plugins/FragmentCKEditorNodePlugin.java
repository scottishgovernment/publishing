package scot.mygov.publishing.plugins;

import org.apache.wicket.model.IModel;
import org.hippoecm.frontend.editor.plugins.linkpicker.LinkPickerDialogConfig;
import org.hippoecm.frontend.model.properties.JcrPropertyValueModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugin.config.impl.JavaPluginConfig;
import org.hippoecm.frontend.plugins.ckeditor.CKEditorNodePlugin;
import org.hippoecm.frontend.plugins.ckeditor.CKEditorPanel;
import org.hippoecm.frontend.plugins.richtext.htmlprocessor.WicketModel;
import org.hippoecm.frontend.plugins.standards.picker.NodePickerControllerSettings;
import org.onehippo.cms7.services.htmlprocessor.model.Model;

import javax.jcr.Node;

public class FragmentCKEditorNodePlugin extends CKEditorNodePlugin {

    public static final IPluginConfig DEFAULT_FRAGMENT_PICKER_CONFIG = createNodePickerSettings(
            "cms-pickers/documents", "ckeditor-fragmentpicker", "hippostd:folder");

    public FragmentCKEditorNodePlugin(final IPluginContext context, final IPluginConfig config) {
        super(context, config);
    }

    @Override
    protected CKEditorPanel createEditPanel(String id, String editorConfigJson) {
        CKEditorPanel editPanel = super.createEditPanel(id, editorConfigJson);

        String editorId = editPanel.getEditorId();
        final FragmentPickerManager linkPicker = createFragmentPicker(editorId);
        final CKEditorPanelFragmentPickerExtension pickerExtension = new CKEditorPanelFragmentPickerExtension(linkPicker);
        editPanel.addExtension(pickerExtension);
        return editPanel;
    }

    private FragmentPickerManager createFragmentPicker(final String editorId) {
        IPluginConfig dialogConfig = LinkPickerDialogConfig.fromPluginConfig(
                getChildPluginConfig(
                        "fragmentpicker",
                        DEFAULT_FRAGMENT_PICKER_CONFIG),
                (JcrPropertyValueModel) getHtmlModel());
        Model<Node> nodeModel = WicketModel.of(getNodeModel());
        FragmentPickerManager richTextLinkPicker = new FragmentPickerManager(getPluginContext(), dialogConfig, nodeModel);
        richTextLinkPicker.setCloseAction(new CKEditorInsertFragmentAction(editorId));
        return richTextLinkPicker;
    }

    private IPluginConfig getChildPluginConfig(final String key, final IPluginConfig defaultConfig) {
        final IPluginConfig childConfig = getPluginConfig().getPluginConfig(key);
        return childConfig != null ? childConfig : defaultConfig;
    }

    private IModel<Node> getNodeModel() {
        return (IModel<Node>) getDefaultModel();
    }

    private static IPluginConfig createNodePickerSettings(final String clusterName, final String lastVisitedKey, final String... lastVisitedNodeTypes) {
        final JavaPluginConfig config = new JavaPluginConfig();
        config.put("cluster.name", clusterName);
        config.put(NodePickerControllerSettings.LAST_VISITED_KEY, lastVisitedKey);
        config.put(NodePickerControllerSettings.LAST_VISITED_NODETYPES, lastVisitedNodeTypes);
        config.put(NodePickerControllerSettings.SELECTABLE_NODETYPES, "publishing:fragment");
        config.makeImmutable();
        return config;
    }
}