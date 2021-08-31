package scot.mygov.publishing.linkrewriter;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.rewriter.impl.SimpleContentRewriter;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

public class FragmentAddingContentRewriter extends SimpleContentRewriter {

    private static final Logger LOG = LoggerFactory.getLogger(FragmentAddingContentRewriter.class);

    private static boolean htmlCleanerInitialized;

    private static HtmlCleaner cleaner;

    @Override
    public String rewrite(String html,
                          Node node,
                          HstRequestContext requestContext,
                          Mount targetMount) {

        try {
            doRewrite(html, node, requestContext, targetMount);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return super.rewrite(html, node, requestContext, targetMount);
    }


    /**
     *
     * when a link is selected
     *
     */

    public String doRewrite(String html,
                            Node node,
                            HstRequestContext requestContext,
                            Mount targetMount) throws RepositoryException {

        Session session = node.getSession();
        Map<String, String> replacements = new HashMap<>();

        TagNode rootNode = getHtmlCleaner().clean(html);
        TagNode [] fragmentDivs = rootNode.getElementsByAttValue("class", "fragment", true, false);

        for (TagNode fragmentDiv : fragmentDivs) {
            String uuid = fragmentDiv.getAttributeByName("");
            Node fragmentHandle = session.getNodeByIdentifier(uuid);
            Node fragmentNode = fragmentHandle.getNode(fragmentHandle.getName());

            String fragmentHtml = fragmentNode.getNode("publishing:html").getProperty("hippostd:content").getString();

            /// run the ewrite on the fragment?
            //String rewrittenFragment = super.rewrite()
            LOG.info("fragment handle is {}", fragmentHandle);

            // get the fragment that has the same version as this node?  will that mean a draft one will show the draft content?

         //   fragmentDiv.addChild();

        }

        return "";
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
}
