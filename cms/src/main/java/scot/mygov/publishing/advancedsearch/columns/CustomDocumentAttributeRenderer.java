package scot.mygov.publishing.advancedsearch.columns;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugins.standards.list.resolvers.AbstractNodeRenderer;

public abstract class CustomDocumentAttributeRenderer extends AbstractNodeRenderer {

    private static final long serialVersionUID = -2853371369162994945L;

    @Override
    protected Component getViewer(String id, final Node node) throws RepositoryException {
        return new Label(id, new MyModel(node)); 
    }

    protected abstract String getObject(CustomAttributes atts);

    class MyModel extends AbstractReadOnlyModel<String> {

        CustomAttributes atts;

        public MyModel(Node node) {
            atts = new CustomAttributes(new JcrNodeModel(node));
        }

        @Override
        public String getObject() {
            return CustomDocumentAttributeRenderer.this.getObject(atts);
        }

        @Override
        public void detach() {
            atts.detach();
        }
        
    }

}
