package scot.mygov.publishing.plugins;

import org.hippoecm.frontend.dialog.ScriptAction;
import org.hippoecm.frontend.plugins.richtext.model.RichTextEditorDocumentLink;

public class CKEditorInsertFragmentAction implements ScriptAction<RichTextEditorDocumentLink> {

    private final String editorId;

    CKEditorInsertFragmentAction(final String editorId) {
        this.editorId = editorId;
    }

    @Override
    public String getJavaScript(final RichTextEditorDocumentLink documentLink) {
        return String.format("CKEDITOR.instances.%s.execCommand('%s', %s);", editorId, "insertFragment", documentLink.toJsString());
    }

}