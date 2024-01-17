package scot.mygov.publishing.htmlrewriter;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.rewriter.impl.SimpleContentRewriter;
import org.hippoecm.hst.core.request.HstRequestContext;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;
import scot.mygov.publishing.beans.StepByStepGuide;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.hippoecm.repository.HippoStdNodeType.HIPPOSTD_STATE;

/**
 * Rewrite html to support publishing specific behaviours:
 * - replace any fragment placeholders whose UUID is that of a publishing:fragment node
 * - add step-by-step-nav params to step by step links
 */
public class PublishingHtmlRewriter extends SimpleContentRewriter {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleContentRewriter.class);

    private static final String FRAGMENT_CLASS = "class=\"fragment\"";

    private static final String DATA_UUID = "data-uuid";

    private static final String HREF = "href";

    private HippoUtils hippoUtils = new HippoUtils();

    @Override
    public String rewrite(String html, Node node, HstRequestContext requestContext,  Mount targetMount) {
        if (html.indexOf(FRAGMENT_CLASS) != -1) {
            html = rewriteFragments(html, node, requestContext, targetMount);
        }

        html = rewriteStepByStep(html, node, requestContext);
        return super.rewrite(html, node, requestContext, targetMount);
    }

    public String rewriteStepByStep(String html, Node node, HstRequestContext requestContext) {
        HippoBean bean = requestContext.getContentBean();
        try {
            if (!isStepByStep(bean) && !isStep(node)) {
                return html;
            }

            String slug = getStepByStepSlug(node);
            return slug != null ? rewriteStepByStepLinks(html, slug) : html;
        } catch (RepositoryException e) {
            LOG.error("failed to rewrite step by step links", e);
            return html;
        }
    }

    String getStepByStepSlug(Node node) throws RepositoryException {
        Node doc = node.getParent().getParent();
        return doc.hasProperty("publishing:slug") ? doc.getProperty("publishing:slug").getString() : null;
    }

    boolean isStep(Node node) throws RepositoryException {
        return startsWith(node.getParent().getName(), "publishing:step");
    }
    boolean isStepByStep(HippoBean bean) {
        return bean != null
                && bean instanceof StepByStepGuide;
    }

    public String rewriteFragments(String html, Node node, HstRequestContext requestContext,  Mount targetMount) {

        // only create if really needed
        StringBuilder sb = new StringBuilder();
        int globalOffset = 0;

        while (html.indexOf(FRAGMENT_CLASS, globalOffset) > -1) {
            int classOffset = html.indexOf(FRAGMENT_CLASS, globalOffset);
            int tagOffset = html.lastIndexOf('<', classOffset);

            // append all content from global index up until the start of the tag
            sb.append(html.substring(globalOffset, tagOffset));

            // find the end of the div.  It contains a nested div and se we need to skip over that too.
            int end = html.indexOf("</", classOffset);
            end = html.indexOf("</", end + 1);
            end = html.indexOf('>', end + 1);

            String uuid = getUuid(html, tagOffset);
            String fragmentContent = getFragmentContent(uuid, node, requestContext, targetMount);
            sb.append(fragmentContent);

            globalOffset = end + 1;
        }

        sb.append(html.substring(globalOffset));
        return sb.toString();
    }

    String getUuid(String html, int offset) {
        int attribStart = html.indexOf(DATA_UUID, offset);

        int openingQuoteIndex = html.indexOf('"', attribStart);
        int closingQuoteIndex = html.indexOf('"', openingQuoteIndex + 1);
        return html.substring(openingQuoteIndex + 1, closingQuoteIndex);
    }

    String getFragmentContent(String uuid, Node node, HstRequestContext requestContext,  Mount targetMount) {

        try {
            Node fragmentHandle = requestContext.getSession().getNodeByIdentifier(uuid);
            Node fragmentVariant = getFragmentVariant(node, fragmentHandle);
            Node fragmentHtmlNode = fragmentVariant.getNode("publishing:content");
            String html = fragmentHtmlNode.getProperty("hippostd:content").getString();

            // rewrite the html so that internal links work
            return super.rewrite(html, fragmentHtmlNode, requestContext, targetMount);
        } catch (RepositoryException e) {
            LOG.warn("Failed to inject fragment {}", uuid, e);
            return "";
        }
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

    String rewriteStepByStepLinks(String html, String slug) {
        Document doc = Jsoup.parse(html);
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
}
