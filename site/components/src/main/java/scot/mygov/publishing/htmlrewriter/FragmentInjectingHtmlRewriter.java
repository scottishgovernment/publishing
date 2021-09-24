package scot.mygov.publishing.htmlrewriter;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.rewriter.impl.SimpleContentRewriter;
import org.hippoecm.hst.core.request.HstRequestContext;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.hippoecm.repository.HippoStdNodeType.HIPPOSTD_STATE;

/**
 * Replace any fragment placeholders whose UUID is that of a publishing:fragment node
 */
public class FragmentInjectingHtmlRewriter extends SimpleContentRewriter {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleContentRewriter.class);

    private HippoUtils hippoUtils = new HippoUtils();

    private static HtmlCleaner cleaner;

    static {
        cleaner = new HtmlCleaner();
        CleanerProperties properties = cleaner.getProperties();
        properties.setOmitHtmlEnvelope(true);
        properties.setTranslateSpecialEntities(false);
        properties.setOmitXmlDeclaration(true);
        properties.setRecognizeUnicodeChars(false);
        properties.setOmitComments(true);
    }

    @Override
    public String rewrite(String html, Node node, HstRequestContext requestContext,  Mount targetMount) {
        TagNode rootNode = cleaner.clean(html);
        rootNode.getElementList(this::isElementWithFragmentClass, true)
                .stream().map(o -> (TagNode) o)
                .forEach(tn -> processFragmentElement(tn, node, requestContext, targetMount));
        html = cleaner.getInnerHtml(rootNode);
        return super.rewrite(html, node, requestContext, targetMount);

    }
    boolean isElementWithFragmentClass(TagNode tagNode) {
        return "fragment".equals(tagNode.getAttributeByName("class"));
    }

    void processFragmentElement(TagNode tagNode, Node htmlNode, HstRequestContext requestContext,  Mount targetMount) {
        try {
            doProcessFragment(tagNode, htmlNode, requestContext, targetMount);
        } catch (RepositoryException e) {
            LOG.error("unexpected exception processing fragments for html", e);
        }
    }

    void doProcessFragment(TagNode tagNode, Node htmlNode, HstRequestContext requestContext,  Mount targetMount) throws RepositoryException {
        Node handle = getFragmentForIdentifier(requestContext.getSession(), tagNode, htmlNode);
        if (!isFragmentHandle(handle)) {
             return;
        }

        String fragment = getFragment(handle, htmlNode, requestContext, targetMount);
        if (StringUtils.isNotBlank(fragment)) {
            // replace the placeholder div with the content of the fragment
            replacePlaceholderWithFragment(tagNode, fragment);
        }
    }

    Node getFragmentForIdentifier(Session session, TagNode tagNode, Node htmlNode) {
        String identifier = tagNode.getAttributeByName("data-uuid");

        try {
            return session.getNodeByIdentifier(identifier);
        } catch (RepositoryException e) {
            LOG.warn("Exception trying to get node while processing fragments, identifier is {}", identifier, e);
            return null;
        }
    }

    boolean isFragmentHandle(Node handle) throws  RepositoryException {
        if (handle == null) {
            return false;
        }
        Node variant = handle.getNode(handle.getName());
        return variant.isNodeType("publishing:fragment");
    }

    String getFragment(Node fragmentHandle, Node htmlNode, HstRequestContext requestContext,  Mount targetMount) throws RepositoryException {
        Node fragmentVariant = getFragmentVariant(htmlNode, fragmentHandle);
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
        } else {
            Node draft = hippoUtils.getVariantWithState(fragmentHandle, "");
            return draft != null
                    ? draft
                    : hippoUtils.getVariantWithState(fragmentHandle, state);
        }
    }

    void replacePlaceholderWithFragment(TagNode tagNode, String fragment) {
        int index = tagNode.getParent().getChildIndex(tagNode);
        TagNode fragmentNode = cleaner.clean(fragment);
        tagNode.getParent().removeChild(tagNode);
        tagNode.getParent().insertChild(index, fragmentNode);
    }
}
