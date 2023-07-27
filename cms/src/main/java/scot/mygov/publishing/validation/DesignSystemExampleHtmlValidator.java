package scot.mygov.publishing.validation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import org.jsoup.safety.Safelist;
import org.onehippo.cms.services.validation.api.ValidationContext;
import org.onehippo.cms.services.validation.api.Validator;
import org.onehippo.cms.services.validation.api.Violation;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class DesignSystemExampleHtmlValidator implements Validator<String> {

    private static final String SCRIPT = "script";

    // defines the html elements that we want to allow in examples.
    // we allow script tags so that we can allow non executable script tags.
    // we then use a node visitor to remove executable ones.
    Safelist safelist = Safelist.relaxed()
            .addTags(SCRIPT, "address","article","fieldset","footer","form","header","input","label","legend",
                    "main","nav","option","section","select","style","svg","textarea","use")

            .preserveRelativeLinks(true)
            .addAttributes(SCRIPT, "type")
            .addAttributes(":all","aria-describedby","aria-hidden","aria-invalid","aria-label","aria-labelledby",
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

            .removeProtocols("img","src","http","https");

    @Override
    public Optional<Violation> validate(ValidationContext context, String html) {
        String parsedHtml = Jsoup.parse(html).body().html();
        String cleanedCode = cleanedHtml(html);
        return cleanedCode.equals(parsedHtml)
            ? Optional.empty()
            : Optional.of(context.createViolation());
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