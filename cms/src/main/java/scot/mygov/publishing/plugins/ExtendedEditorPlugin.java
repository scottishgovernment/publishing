package scot.mygov.publishing.plugins;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.hippoecm.frontend.editor.builder.FieldEditor;
import org.hippoecm.frontend.editor.editor.EditorPlugin;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.types.IFieldDescriptor;
import org.hippoecm.frontend.types.ITypeDescriptor;

public class ExtendedEditorPlugin extends EditorPlugin {


    public ExtendedEditorPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    public void renderHead(IHeaderResponse response) {

        response.render(OnDomReadyHeaderItem.forScript("$(\".hippo-hint-field\").each(function() {$(this).hide();$(this).after(\"<p class='hint-text'>\"+$(this).attr(\"data-original-title\") +\"</p>\");});"));
    }
}
