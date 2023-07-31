package scot.mygov.publishing.validation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.jsoup.nodes.Node;
import org.jsoup.safety.Safelist;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class DesignSystemExampleHtmlValidator implements Validator<String> {

    private static final String DATA_THRESHOLD = "data-threshold";

    private static final String SCRIPT = "script";

    private static final String TEXTAREA = "textarea";

    private static final String VALUE = "value";

    private static Pattern HTML_COMMENT_PATTERN = Pattern.compile("<!--[\\s\\S]*?-->");

    // defines the html elements that we want to allow in examples.
    // we allow script tags so that we can allow non executable script tags.
    // we then use a node visitor to remove executable ones.
    Safelist safelist = Safelist.relaxed()
            .addTags(SCRIPT, "address", "article","aside","br","fieldset","footer","form","header","input","label","legend",
                    "main","mark","nav","option","section","select","style","svg",TEXTAREA,"use")

            .preserveRelativeLinks(true)
            .addAttributes(SCRIPT, "type")
            .addAttributes(":all","aria-controls","aria-current","aria-describedby","aria-hidden","aria-invalid","aria-label","aria-labelledby",
                    "aria-required","class","data-label","data-module","id","role","tabindex","translate")
            .addAttributes("button", "disabled", "type")
            .addAttributes("div","data-symbol", DATA_THRESHOLD)
            .addAttributes("form","action","method")
            .addAttributes("img","loading","sizes","srcset")
            .addAttributes("input","aria-autocomplete","aria-expanded","aria-owns","autocomplete",
                    "checked","data-behaviour", DATA_THRESHOLD,"haspopup","maxlength","name","placeholder","required",
                    "type", VALUE, "hidden")
            .addAttributes(TEXTAREA,DATA_THRESHOLD, "data-validation", "maxlength","placeholder","required", VALUE)
            .addAttributes("label","for")
            .addAttributes("ol","data-total","start")
            .addAttributes("option","selected", VALUE)
            .addAttributes("select","name")
            .addAttributes("svg","aria-hidden")
            .addAttributes("table","data-smallscreen")
            .addAttributes("td","align")
            .addAttributes("th","align")
            .addAttributes(TEXTAREA,"rows")
            .addAttributes("use","href")

            .removeProtocols("img","src","http","https");

    @Override
    public Optional<Violation> validate(ValidationContext context, String html) {
        String htmlWithoutComments = removeComments(html);
        Document htmlDocument = Jsoup.parse(htmlWithoutComments);
        String parsedHtml = htmlDocument.body().html();
        String cleanedCode = cleanedHtml(htmlWithoutComments);
        return equalsIgnoringWhitespace(cleanedCode, parsedHtml)
            ? Optional.empty()
            : Optional.of(context.createViolation());
    }

    boolean equalsIgnoringWhitespace(String left, String right) {
        String leftStripped = left.replaceAll("\\s", "");
        String rightStripped = right.replaceAll("\\s", "");
        return leftStripped.equals(rightStripped);
    }

    String cleanedHtml(String html) {
        String cleaned = Jsoup.clean(html, "http://", safelist);
        return removeExecutableScript(cleaned);
    }

    String removeExecutableScript(String code) {
        // using the xml parse prevents Jsoup from adding html / head / body elements.
        Document doc = Jsoup.parse(code);
        doc.traverse(this::visit);
        return doc.body().html();
    }

    void visit(Node node, int depth) {
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

    public String removeComments(String htmlString) {
        return HTML_COMMENT_PATTERN.matcher(htmlString).replaceAll("");
    }
}
