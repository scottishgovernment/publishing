package scot.mygov.publishing.plugins;

import org.hippoecm.frontend.dialog.Dialog;
import org.hippoecm.frontend.dialog.DialogManager;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.richtext.dialog.links.DocumentBrowserDialog;
import org.hippoecm.frontend.plugins.richtext.dialog.links.RichTextEditorLinkService;
import org.hippoecm.frontend.plugins.richtext.htmlprocessor.WicketNodeFactory;
import org.hippoecm.frontend.plugins.richtext.model.RichTextEditorDocumentLink;
import org.hippoecm.frontend.plugins.richtext.model.RichTextEditorInternalLink;
import org.onehippo.cms7.services.htmlprocessor.model.Model;
import org.onehippo.cms7.services.htmlprocessor.richtext.link.RichTextLinkFactoryImpl;

import javax.jcr.Node;
import java.util.Map;

public class FragmentPickerManager extends DialogManager<RichTextEditorDocumentLink> {
    private final RichTextEditorLinkService linkService;

    public FragmentPickerManager(IPluginContext context, IPluginConfig config, Model<Node> nodeModel) {
        super(context, config);
        RichTextLinkFactoryImpl linkFactory = new RichTextLinkFactoryImpl(nodeModel, WicketNodeFactory.INSTANCE);
        this.linkService = new RichTextEditorLinkService(linkFactory);
    }

    protected Dialog<RichTextEditorDocumentLink> createDialog(IPluginContext context, IPluginConfig config, Map<String, String> parameters) {
        RichTextEditorInternalLink internalLink = this.linkService.create(parameters);
        org.apache.wicket.model.Model model = org.apache.wicket.model.Model.of(internalLink);
        return new DocumentBrowserDialog(context, config, model);
    }
}
