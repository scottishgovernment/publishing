package scot.mygov.publishing.components.eventbrite.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextAndHtml {

    String text;

    String html;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
