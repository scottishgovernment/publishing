package scot.mygov.publishing.htmlrewriter;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.rewriter.impl.SimpleContentRewriter;
import org.hippoecm.hst.core.request.HstRequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.HippoUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static org.hippoecm.repository.HippoStdNodeType.HIPPOSTD_STATE;

/**
 * Replace any fragment placeholders whose UUID is that of a publishing:fragment node
 */
public class FragmentInjectingHtmlRewriter extends SimpleContentRewriter {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleContentRewriter.class);

    private HippoUtils hippoUtils = new HippoUtils();

    private static final String FRAGMENT_CLASS = "class=\"fragment\"";

    private static final String DATA_UUID = "data-uuid";

    @Override
    public String rewrite(String html, Node node, HstRequestContext requestContext,  Mount targetMount) {
        if (html.indexOf(FRAGMENT_CLASS) == -1) {
            return super.rewrite(html, node, requestContext, targetMount);
        }

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
        return super.rewrite(sb.toString(), node, requestContext, targetMount);
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
        } else {
            Node draft = hippoUtils.getVariantWithState(fragmentHandle, "");
            return draft != null
                    ? draft
                    : hippoUtils.getVariantWithState(fragmentHandle, state);
        }
    }
}
