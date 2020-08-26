package scot.mygov.publishing.plugins;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.hippoecm.frontend.editor.editor.EditorPlugin;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;

public class ExtendedEditorPlugin extends EditorPlugin {

    public ExtendedEditorPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    public void renderHead(IHeaderResponse response) {

        response.render(OnLoadHeaderItem.forScript("$(\".hippo-hint-field\").each(function() {if(!$(this).next().hasClass('hint-text')){$(this).hide();$(this).after(\"<p class='hint-text'>\"+$(this).attr(\"data-original-title\") +\"</p>\");}});"));
    }
}
