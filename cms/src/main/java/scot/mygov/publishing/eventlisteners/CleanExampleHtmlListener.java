package scot.mygov.publishing.eventlisteners;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Safelist;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;

public class CleanExampleHtmlListener {

    private static final Logger LOG = LoggerFactory.getLogger(CleanExampleHtmlListener.class);

    private static final String CODE = "publishing:code";

    private static final String SCRIPT = "script";

    Session session;

    // defines the html elements that we want to allow in examples.
    // we allow script tags so that we can allow non executable script tags.
    // we then use a node visitor to remove executable ones.
    Safelist safelist = Safelist.relaxed()
            .addTags(SCRIPT, "address","fieldset","footer","form","header","input","label","legend",
                    "main","nav","option","section","select","style","svg","textarea","use")

            .addAttributes(SCRIPT, "type")
            .addAttributes(":all","aria-describedby","aria-invalid","aria-label","aria-labelledby",
                    "aria-required","class","data-module","id","role","tabindex","translate")
            .addAttributes("button","disabled","type")
            .addAttributes("form","action","method")
            .addAttributes("img","loading","sizes","srcset")
            .addAttributes("input","aria-autocomplete","aria-expanded","aria-owns","autocomplete",
                    "checked","data-behaviour","haspopup","maxlength","name","placeholder","required",
                    "type","value","hidden")
            .addAttributes("label","for")
            .addAttributes("ol","data-total","start")
            .addAttributes("option","selected")
            .addAttributes("svg","aria-hidden")
            .addAttributes("table","data-smallscreen")
            .addAttributes("textarea","rows")
            .addAttributes("use","href")

            .addProtocols("use","href","http","https")
            .addProtocols("img","srcset","http","https")

            .preserveRelativeLinks(true);

    CleanExampleHtmlListener(Session session) {
        this.session = session;
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {

        if (!canHandle(event)) {
            return;
        }
        try {
            doHandleEvent(event);
        } catch (RepositoryException e) {
            ensureRefreshFalse(session);
            LOG.error(
                    "error trying to clean code for event msg={}, action={}, event={}, result={}", e.getMessage(),
                    event.action(), event.category(), event.result(), e);
        }
    }

    boolean canHandle(HippoWorkflowEvent event) {
        return "publishing:dsexample".equals(event.documentType())
                && "commitEditableInstance".equals(event.action());
    }

    void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        Node handle = session.getNode(event.subjectPath());
        new HippoUtils().apply(handle.getNodes(handle.getName()), this::cleanCode);
        session.save();
    }

    void cleanCode(Node node) throws RepositoryException {
        String code = node.getProperty(CODE).getString();
        String cleanedCode = Jsoup.clean(code, safelist);
        String withoutExecutableScript = removeExecutableScript(cleanedCode);
        node.setProperty(CODE, withoutExecutableScript);
    }

    String removeExecutableScript(String code) {
        // using the xml parse prevents Jsoup from adding html / head / body elements.
        Document doc = Jsoup.parse(code, Parser.xmlParser());
        doc.traverse(this::visit);
        return doc.html();
    }

    void visit(org.jsoup.nodes.Node node, int depth) {
        if (isExecutableScript(node)) {
            node.remove();
        }
    }

    boolean isExecutableScript(org.jsoup.nodes.Node node) {
        if (!(node instanceof Element)) {
            return false;
        }

        Element element = (Element) node;
        if (!equalsIgnoreCase(element.tagName(), SCRIPT)) {
            return false;
        }

        String type = element.attr("type");
        return !"application/ld+json".equalsIgnoreCase(type);
    }
}
