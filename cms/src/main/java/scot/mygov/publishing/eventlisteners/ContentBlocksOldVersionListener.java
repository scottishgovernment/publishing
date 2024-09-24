package scot.mygov.publishing.eventlisteners;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static scot.mygov.publishing.eventlisteners.EventListerUtil.ensureRefreshFalse;

public class ContentBlocksOldVersionListener {

    private static final Logger LOG = LoggerFactory.getLogger(ContentBlocksOldVersionListener.class);

    private static final String DOCBASE = "hippo:docbase";

    private static final String FRAGMENT = "fragment";

    private static final String ACCORDION = "accordion";

    private static final String PUBLISHING_CONTENT = "publishing:content";

    private static final String HIPPOSTD_CONTENT = "hippostd:content";

    Session session;

    private static Map<String, String> mapping = new HashMap<>();

    static {
        mapping.put(PUBLISHING_CONTENT, "publishing:contentBlocks");
        mapping.put("publishing:prologue", "publishing:prologueContentBlocks");
        mapping.put("publishing:epilogue", "publishing:epilogueContentBlocks");
        mapping.put("publishing:additionalContent", "publishing:additionalContentBlocks");
        mapping.put("publishing:noResultsMessage", "publishing:noResultsMessageContentBlocks");
        mapping.put("publishing:blankSearchQueryMessage", "publishing:blankSearchQueryMessageContentBlocks");
        mapping.put("publishing:notices", "publishing:noticesContentBlocks");
        mapping.put("publishing:organisationstructure", "publishing:organisationstructureContentBlocks");
        mapping.put("publishing:description", "publishing:descriptionContentBlocks");
        mapping.put("publishing:answer", "publishing:answerContentBlocks");
    }
    public ContentBlocksOldVersionListener(Session session) {
        this.session = session;
    }

    @Subscribe
    public void handleEvent(HippoWorkflowEvent event) {
        if (!StringUtils.equals(event.action(), "restoreVersionToBranch")) {
            return;
        }

        try {
            doHandleEvent(event);
            session.save();
        } catch (RepositoryException e) {
            ensureRefreshFalse(session);
            LOG.error(
                    "error trying to restore versionevent msg={}, action={}, event={}, result={}", e.getMessage(),
                    event.action(), event.category(), event.result(), e);
        }
    }

    void doHandleEvent(HippoWorkflowEvent event) throws RepositoryException {
        Node subject = session.getNode(event.subjectPath());
        NodeIterator it = subject.getNodes(subject.getName());
        while (it.hasNext()) {
            Node variant = it.nextNode();
            for (String fieldname : mapping.keySet()) {
                if (hasOldContentField(variant, fieldname)) {
                    Node html = variant.getNode(fieldname);
                    fix(html);
                }
            }
        }
    }

    boolean hasOldContentField(Node variant, String fieldname) throws RepositoryException {
        if (!variant.hasNode(fieldname)) {
            return false;
        }

        Node htmlNode = variant.getNode(fieldname);
        String content = htmlNode.getProperty(HIPPOSTD_CONTENT).getString();
        return isNotBlank(content);
    }

    void fix(Node htmlnode) throws RepositoryException {
        String html = htmlnode.getProperty(HIPPOSTD_CONTENT).getString();
        List<Chunk> chunks = splitHtmlByFragment(html);
        Node instance = htmlnode.getParent();
        String name = mapping.get(htmlnode.getName());
        for (Chunk chunk : chunks) {
            addChunk(chunk, instance, htmlnode, name);
        }
        htmlnode.setProperty(HIPPOSTD_CONTENT, "");
    }

    void addChunk(Chunk chunk, Node instance, Node htmlnode, String name) throws RepositoryException {
        switch (chunk.getType()) {
            case "html" :
                addRichTextChunk(chunk, instance, htmlnode, name);
                break;
            case ACCORDION:
                addAccordionChunk(chunk, instance, htmlnode, name);
                break;
            case FRAGMENT:
                addFragmentChunk(chunk, instance, htmlnode, name);
                break;
            case "lafinder" :
                addLAFinderChunk(chunk, instance, htmlnode, name);
                break;
            default:
                LOG.warn("skipping chunk: {}", chunk.type);
        }
    }

    void addLAFinderChunk(Chunk chunk, Node instance, Node htmlnode, String name) throws RepositoryException {
        Node compound = blockNode(instance, name, "publishing:cb_laFinder");
        Document document = Jsoup.parse(chunk.getHtml());

        Elements labels = document.select(".ds_label");
        String label = labels.first().text();
        compound.setProperty("publishing:title", label);
        Elements options = document.select("option");
        int index = 0;
        for (Element option : options) {
            if (index == 0) {
                compound.setProperty("publishing:placeholder", option.text());
            } else {
                Node link = compound.addNode("publishing:links", "publishing:linkwithlabel");
                String id = option.attr("data-id");
                String url = document.select("#dd-" + id).attr("href");
                link.setProperty("publishing:label", option.text());
                link.setProperty("publishing:url", url);
            }
            index++;
        }
    }

    void addRichTextChunk(Chunk chunk, Node instance, Node htmlnode, String name) throws RepositoryException {
        addRichTextChunk(chunk.getHtml(), instance, htmlnode, name);
    }

    Node addRichTextChunk(String htmlcontent, Node instance, Node htmlnode, String name) throws RepositoryException {
        Node compound = blockNode(instance, name, "publishing:cb_richtext");

        // create the contetn node
        Node html = compound.addNode(PUBLISHING_CONTENT, "hippostd:html");
        html.setProperty(HIPPOSTD_CONTENT, htmlcontent);
        NodeIterator it = htmlnode.getNodes();
        while (it.hasNext()) {
            Node facet = it.nextNode();
            if (htmlcontent.contains(facet.getName())) {
                Node newfacet = html.addNode(facet.getName(), "hippo:facetselect");
                newfacet.setProperty(DOCBASE, facet.getProperty(DOCBASE).getString());
                newfacet.setProperty("hippo:facets", new String []{});
                newfacet.setProperty("hippo:modes", new String []{});
                newfacet.setProperty("hippo:values", new String []{});
            }
        }
        return html;
    }

    void addAccordionChunk(Chunk chunk, Node instance, Node htmlnode, String name) throws RepositoryException {
        Node compound = blockNode(instance, name, "publishing:cb_accordion");
        Document document = Jsoup.parse(chunk.getHtml());
        Elements items = document.select(".ds_accordion-item");
        for (Element item : items) {
            String title = item.select(".ds_accordion-item__title").text();
            String content = item.select(".ds_accordion-item__body").html();
            Node acc = compound.addNode("publishing:items", "publishing:cb_accordionpanel");
            acc.setProperty("publishing:title", title);
            addRichTextChunk(content, acc, htmlnode, name);
        }
        session.save();
    }

    Node blockNode(Node parent, String name, String type) throws RepositoryException {
        Node compound = parent.addNode(name, type);
        compound.addMixin("hippo:container");
        compound.addMixin("hippostd:container");
        compound.addMixin("hippostd:relaxed");
        compound.setProperty("publishing:noindex", false);
        return compound;
    }

    void addFragmentChunk(Chunk chunk, Node instance, Node htmlnode, String name) throws RepositoryException {
        Document document = Jsoup.parse(chunk.getHtml());
        String docbase = document.select("div").first().attr("data-uuid");
        Node compound = blockNode(instance, name, "publishing:cb_fragment");
        Node mirror = compound.addNode("publishing:fragment", "hippo:mirror");
        mirror.setProperty(DOCBASE, docbase);
        session.save();
    }

    public static List<Chunk> splitHtmlByFragment(String html) {
        List<Chunk> chunks = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements bodyChildren = doc.body().children();
        StringBuilder accordionChunkBuilder = new StringBuilder();
        StringBuilder htmlChunkBuilder = new StringBuilder();

        for (int i = 0; i < bodyChildren.size(); i++) {
            Element currentElement = bodyChildren.get(i);
            if (currentElement.hasClass(FRAGMENT)) {
                processChunk(chunks, htmlChunkBuilder, "html");
                processChunk(chunks, accordionChunkBuilder, ACCORDION);
                chunks.add(new Chunk(currentElement.outerHtml(), FRAGMENT));
            } else if (currentElement.hasClass("ds_accordion-item")) {
                processChunk(chunks, htmlChunkBuilder, "html");
                accordionChunkBuilder.append(currentElement.outerHtml());
            } else if (currentElement.hasClass("finder-hero")) {
                processChunk(chunks, htmlChunkBuilder, "html");
                processChunk(chunks, accordionChunkBuilder, ACCORDION);
                chunks.add(new Chunk(currentElement.outerHtml(), "lafinder"));
            } else {
                processChunk(chunks, accordionChunkBuilder, ACCORDION);
                htmlChunkBuilder.append(currentElement.outerHtml());
            }
        }

        processChunk(chunks, htmlChunkBuilder, "html");
        processChunk(chunks, accordionChunkBuilder, ACCORDION);

        return chunks;
    }

    private static void processChunk(List<Chunk> chunks, StringBuilder builder, String type) {
        if (builder.length() > 0) {
            chunks.add(new Chunk(builder.toString(), type));
            builder.setLength(0);
        }
    }

    public static class Chunk {
        private final String html;
        private final String type;

        public Chunk(String html, String type) {
            this.html = html;
            this.type = type;
        }

        public String getHtml() {
            return html;
        }

        public String getType() {
            return type;
        }
    }


}
