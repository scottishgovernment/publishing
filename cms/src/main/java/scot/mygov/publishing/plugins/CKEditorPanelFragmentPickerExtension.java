package scot.mygov.publishing.plugins;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.wicket.behavior.Behavior;
import org.hippoecm.frontend.dialog.DialogManager;
import org.hippoecm.frontend.plugins.ckeditor.CKEditorPanelExtension;
import org.hippoecm.frontend.plugins.ckeditor.hippopicker.HippoPicker;

import java.io.IOException;
import java.util.Arrays;

public class CKEditorPanelFragmentPickerExtension implements CKEditorPanelExtension {

    private final FragmentPickerManager fragmentPicker;

    public CKEditorPanelFragmentPickerExtension(final FragmentPickerManager fragmentPicker) {
        this.fragmentPicker = fragmentPicker;
    }

    @Override
    public void addConfiguration(final ObjectNode editorConfig) throws IOException {
        addInternalLinkPickerConfiguration(editorConfig);
    }

    private void addInternalLinkPickerConfiguration(final ObjectNode editorConfig) {
        final ObjectNode pickerPluginConfig = editorConfig.with(HippoPicker.CONFIG_KEY);
        final ObjectNode config = pickerPluginConfig.with("fragment");
        addCallbackUrl(config, HippoPicker.InternalLink.CONFIG_CALLBACK_URL, fragmentPicker);
    }

    private static void addCallbackUrl(final ObjectNode config, final String key, final DialogManager provider) {
        final String callbackUrl = provider.getBehavior().getCallbackUrl().toString();
        config.put(key, callbackUrl);
    }

    @Override
    public Iterable<Behavior> getBehaviors() {
        return Arrays.asList(fragmentPicker.getBehavior());
    }

    @Override
    public void detach() {
        fragmentPicker.detach();
    }
}