package scot.mygov.publishing.components.funnelback.postprocess;

import org.junit.Test;
import scot.gov.publishing.hippo.funnelback.model.Exhibit;
import scot.gov.publishing.hippo.funnelback.model.FunnelbackSearchResponse;

import static org.junit.Assert.assertEquals;

public class CuratorPostProcessorTest {

    @Test
    public void advertsAndMessagesSegregatedAsExpected() {
        // ARRANGE
        CuratorPostProcessor sut = new CuratorPostProcessor();
        FunnelbackSearchResponse input = responseWithMessagesAndAdverts();

        // ACT
        sut.process(input);

        // ASSERT
        assertEquals(1, input.getResponse().getCurator().getAdvertExhibits().size());
        assertEquals(2, input.getResponse().getCurator().getSimpleHtmlExhibits().size());
        assertEquals("message1", input.getResponse().getCurator().getSimpleHtmlExhibits().get(0).getMessageHtml());
        assertEquals("message2", input.getResponse().getCurator().getSimpleHtmlExhibits().get(1).getMessageHtml());
        assertEquals("google description", input.getResponse().getCurator().getAdvertExhibits().get(0).getDescriptionHtml());
    }

    @Test
    public void htmlMessageCleansedAsExpected() {
        // ARRANGE
        CuratorPostProcessor sut = new CuratorPostProcessor();
        FunnelbackSearchResponse input = responseWithHtmlMessages();

        // ACT
        sut.process(input);

        // ASSERT
        assertEquals(cleanedMessage(), input.getResponse().getCurator().getSimpleHtmlExhibits().get(0).getMessageHtml());
    }

    @Test
    public void advertWithHtmlCleanedAsExpected() {
        // ARRANGE
        CuratorPostProcessor sut = new CuratorPostProcessor();
        FunnelbackSearchResponse input = responseWithAdvertsContainingHtml();

        // ACT
        sut.process(input);

        // ASSERT
        assertEquals(cleanedMessage(), input.getResponse().getCurator().getAdvertExhibits().get(0).getTitleHtml());
        assertEquals(cleanedMessage(), input.getResponse().getCurator().getAdvertExhibits().get(0).getDescriptionHtml());
    }

    FunnelbackSearchResponse responseWithHtmlMessages() {
        FunnelbackSearchResponse response = new FunnelbackSearchResponse();
        response.getResponse().getCurator().getExhibits().add(messageExhibit(htmlMessage()));
        response.getResponse().getCurator().getExhibits().add(messageExhibit(htmlMessage()));
        return response;
    }

    String htmlMessage() {
        return "<div>This is n <b>HTML</b> <span class=\"blah\">message</span> <img src=\"path\"/></div>";
    }

    String cleanedMessage() {
        return "This is n <b>HTML</b> <span>message</span>";
    }

    FunnelbackSearchResponse responseWithMessagesAndAdverts() {
        FunnelbackSearchResponse response = new FunnelbackSearchResponse();
        response.getResponse().getCurator().getExhibits().add(messageExhibit("message1"));
        response.getResponse().getCurator().getExhibits().add(messageExhibit("message2"));
        response.getResponse().getCurator().getExhibits().add(advertExhibit1());
        return response;
    }

    FunnelbackSearchResponse responseWithAdvertsContainingHtml() {
        FunnelbackSearchResponse response = new FunnelbackSearchResponse();
        response.getResponse().getCurator().getExhibits().add(advertExhibitWithHtml());
        return response;
    }

    Exhibit messageExhibit(String message) {
        Exhibit exhibit = new Exhibit();
        exhibit.setMessageHtml(message);
        return exhibit;
    }

    Exhibit advertExhibit1() {
        Exhibit exhibit = new Exhibit();
        exhibit.setDisplayUrl("https://www.google.com/");
        exhibit.setLinkUrl("https://www.google.com/");
        exhibit.setDescriptionHtml("google description");
        exhibit.setTitleHtml("google title");
        return exhibit;
    }

    Exhibit advertExhibitWithHtml() {
        Exhibit exhibit = new Exhibit();
        exhibit.setDisplayUrl("https://www.google.com/");
        exhibit.setLinkUrl("https://www.google.com/");
        exhibit.setDescriptionHtml(htmlMessage());
        exhibit.setTitleHtml(htmlMessage());
        return exhibit;
    }
}
