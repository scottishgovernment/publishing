package scot.mygov.publishing.htmlrewriter;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.hst.content.rewriter.impl.SimpleContentRewriter;
import org.hippoecm.hst.core.request.HstRequestContext;

import org.hippoecm.hst.utils.MessageUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.hippoecm.repository.HippoStdNodeType.HIPPOSTD_STATE;
import static scot.gov.variables.VariablesHelper.getVariablesResourceBundle;

/**
 * Rewrite html to support publishing specific behaviours:
 * - replace any fragment placeholders whose UUID is that of a publishing:fragment node
 * - add step-by-step-nav params to step by step links
 * - interpolate variable contained within [[ ]]'s
 */
public class PublishingHtmlRewriter extends SimpleContentRewriter {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleContentRewriter.class);

    private static final String FRAGMENT_CLASS = "class=\"fragment\"";

    private static final String HREF = "href";

    private static final String SLUG_PROPERTY = "publishing:slug";

    private HippoUtils hippoUtils = new HippoUtils();

    @Override
    public String rewrite(String html, Node node, HstRequestContext requestContext,  Mount targetMount) {
        try {
            return doRewrite(html, node, requestContext, targetMount);
        } catch (RepositoryException e) {
            LOG.error("failed to rewrite html ", e);
            return html;
        }
    }

    public String doRewrite(String html, Node node, HstRequestContext requestContext,  Mount targetMount)
            throws RepositoryException {
        if (hasFragments(html) || isStepByStep(node)) {
            Document doc = Jsoup.parse(html);
            rewriteFragments(doc, node, requestContext, targetMount);
            rewriteStepByStep(doc, node);
            html = doc.html();
        }
        html = replaceVariables(html);
        return super.rewrite(html, node, requestContext, targetMount);
    }

    boolean hasFragments(String html) {
        return html.indexOf(FRAGMENT_CLASS) != -1;
    }

    public void rewriteStepByStep(Document doc, Node node) throws RepositoryException {
        String slug = getStepByStepSlug(node);
        if (slug != null) {
            rewriteStepByStepLinks(doc, slug);
        }
    }

    String getStepByStepSlug(Node node) throws RepositoryException {
        Node stepByStep = getStepByStep(node);
        return stepByStep != null ? stepByStep.getProperty(SLUG_PROPERTY).getString() : null;
    }

    Node getStepByStep(Node node) throws RepositoryException {
        if (node.isNodeType("hippo:handle")) {
            return null;
        }

        return node.hasProperty(SLUG_PROPERTY) ? node : getStepByStep(node.getParent());
    }

    boolean isStepByStep(Node node) throws RepositoryException {
        try {
            Node stepByStep = getStepByStep(node);
            return  stepByStep != null;
        } catch (RepositoryException e) {
            LOG.error("arg", e);
            return false;
        }
    }

    public void rewriteFragments(Document doc, Node node, HstRequestContext requestContext,  Mount targetMount)
            throws RepositoryException {
        List<Element> fragments = doc.select("div.fragment");
        for (Element fragment : fragments) {
            rewriteFragment(fragment, node, requestContext, targetMount);
        }
    }

    public void rewriteFragment(Element fragDiv, Node node, HstRequestContext requestContext,  Mount targetMount)
            throws RepositoryException {
        String uuid = fragDiv.attr("data-uuid");
        String fragmentContent = getFragmentContent(uuid, node,requestContext, targetMount);
        fragDiv.after(fragmentContent);
        fragDiv.remove();
    }

    String getFragmentContent(String uuid, Node node, HstRequestContext requestContext,  Mount targetMount) throws RepositoryException {

        Node fragmentHandle = requestContext.getSession().getNodeByIdentifier(uuid);
        Node fragmentVariant = getFragmentVariant(node, fragmentHandle);
        Node fragmentHtmlNode = fragmentVariant.getNode("publishing:content");
        String html = fragmentHtmlNode.getProperty("hippostd:content").getString();

        // rewrite the html so that internal links work
        return super.rewrite(html, fragmentHtmlNode, requestContext, targetMount);

    }

    Node getFragmentVariant(Node htmlNode, Node fragmentHandle) throws RepositoryException {
        Node contentItem = htmlNode.getParent();
        String state = contentItem.getProperty(HIPPOSTD_STATE).getString();
        if ("published".equals(state)) {
            return hippoUtils.getVariantWithState(fragmentHandle, state);
        }
        Node draft = hippoUtils.getVariantWithState(fragmentHandle, "");
        return draft != null
                ? draft
                : hippoUtils.getVariantWithState(fragmentHandle, state);
    }

    String rewriteStepByStepLinks(Document doc, String slug) {
        Elements links = doc.select("a[href]");
        links.stream().filter(this::isLocal).forEach(link -> rewrite(link, slug));
        return doc.html();
    }

    boolean isLocal(Element link) {
        return !startsWith(link.attr(HREF), "http");
    }

    void rewrite(Element link, String slug) {
        String href = link.attr(HREF);
        StringBuilder newHref =
                new StringBuilder(href)
                        .append(href.contains("?") ? '&' : '?')
                        .append("step-by-step-nav=")
                        .append(slug);
        link.attr(HREF, newHref.toString());
    }

    private String replaceVariables(final String html) throws RepositoryException {
        return (html.contains("[[") && html.contains("]]")) ? getReplaceTextWithValue(html) : html;
    }

    public static String getReplaceTextWithValue(String text) throws RepositoryException {
        Session session = RequestContextProvider.get().getSession();
        return MessageUtils.replaceMessagesByBundle(getVariablesResourceBundle(session), text, "[[", "]]");
    }

}
