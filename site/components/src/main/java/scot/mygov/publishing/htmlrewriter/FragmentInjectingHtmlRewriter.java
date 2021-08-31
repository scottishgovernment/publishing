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


import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.hippoecm.repository.HippoStdNodeType.HIPPOSTD_STATE;

/**
 * Replace any fragment div's with the fragment refered to.  Fragments divs look like:
 * <div class="fragment" data-fragment-uuid="<uuid if handle>">Fragment</div>
 */
public class FragmentInjectingHtmlRewriter extends SimpleContentRewriter {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleContentRewriter.class);

    protected static final String DATA_FRAGMENT_UUID = "data-fragment-uuid";

    protected static final String FRAGMENT_CLASS = "fragment";

    private HippoUtils hippoUtils = new HippoUtils();

    private static boolean htmlCleanerInitialized;

    private static HtmlCleaner cleaner;

    @Override
    public String rewrite(String html, Node node, HstRequestContext requestContext,  Mount targetMount) {

        // do not parse the html if there are no fragments
        if (!html.contains(DATA_FRAGMENT_UUID)) {
            return super.rewrite(html, node, requestContext, targetMount);
        }

        TagNode rootNode = getHtmlCleaner().clean(html);
        rootNode.getElementList(this::isFragment, true)
                .stream().map(o -> (TagNode) o)
                .forEach(tn -> processFragmentDiv(tn, node, requestContext, targetMount));
        html = getHtmlCleaner().getInnerHtml(rootNode);
        return super.rewrite(html, node, requestContext, targetMount);
    }

    private static synchronized void initCleaner() {
        if (!htmlCleanerInitialized) {
            cleaner = new HtmlCleaner();
            CleanerProperties properties = cleaner.getProperties();
            properties.setOmitHtmlEnvelope(true);
            properties.setTranslateSpecialEntities(false);
            properties.setOmitXmlDeclaration(true);
            properties.setRecognizeUnicodeChars(false);
            properties.setOmitComments(true);
            htmlCleanerInitialized = true;
        }
    }

    protected static HtmlCleaner getHtmlCleaner() {
        if (!htmlCleanerInitialized) {
            initCleaner();
        }

        return cleaner;
    }

    public boolean isFragment(TagNode tagNode) {
        return contains(tagNode.getAttributeByName("class"), FRAGMENT_CLASS);
    }

    void processFragmentDiv(TagNode tagNode, Node htmlNode, HstRequestContext requestContext,  Mount targetMoun) {
        String fragmentUuid = tagNode.getAttributeByName(DATA_FRAGMENT_UUID);
        String fragment = getFragment(fragmentUuid, htmlNode, requestContext, targetMoun);
        if (StringUtils.isNotBlank(fragment)) {
            int index = tagNode.getParent().getChildIndex(tagNode);
            TagNode fragmentNode = getHtmlCleaner().clean(fragment);
            tagNode.getParent().removeChild(tagNode);
            tagNode.getParent().insertChild(index, fragmentNode);
        }
    }

    String getFragment(String fragmentUuid, Node htmlNode, HstRequestContext requestContext,  Mount targetMount) {
        try {
            Session session = requestContext.getSession();
            Node fragmentHandle = session.getNodeByIdentifier(fragmentUuid);
            Node fragmentVariant = getFragmentVariant(htmlNode, fragmentHandle);
            Node fragmentHtmlNode = fragmentVariant.getNode("publishing:html");
            String html = fragmentHtmlNode.getProperty("hippostd:content").getString();

            // rewrite the html so that internal links work
            return super.rewrite(html, fragmentHtmlNode, requestContext, targetMount);
        } catch (ItemNotFoundException e) {
            LOG.warn("Fragment does not exist {}", fragmentUuid, e);
            return "";
        } catch (RepositoryException e) {
            LOG.warn("Unexpected exception fetching fragment {}", fragmentUuid, e);
            return "";
        }
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
}