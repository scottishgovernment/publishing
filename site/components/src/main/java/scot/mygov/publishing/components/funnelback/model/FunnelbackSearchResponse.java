package scot.mygov.publishing.components.funnelback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FunnelbackSearchResponse {

    Question question = new Question();

    Response response = new Response();

    String error = "";

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}