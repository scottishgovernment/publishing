package scot.mygov.publishing.components.funnelback;

import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import scot.gov.publishing.hippo.funnelback.model.Pagination;
import scot.gov.publishing.hippo.funnelback.model.Question;
import scot.gov.publishing.hippo.funnelback.model.Response;
import scot.gov.publishing.hippo.funnelback.model.ResultPacket;

/**
 * Created by z418868 on 01/07/2022.
 */
public class SearchResponse {

    enum Type { BLOOMREACH, FUNNELBACK }

    Type type;

    Question question;

    Response response;

    Pagination pagination;

    HippoBeanIterator bloomreachResults;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

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

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public HippoBeanIterator getBloomreachResults() {
        return bloomreachResults;
    }

    public void setBloomreachResults(HippoBeanIterator bloomreachResults) {
        this.bloomreachResults = bloomreachResults;
    }

    public static SearchResponse blankSearchResponse() {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setType(SearchResponse.Type.BLOOMREACH);

        Question question = new Question();
        question.setOriginalQuery("");
        question.setQuery("");
        searchResponse.setQuestion(question);

        Response response = new Response();
        searchResponse.setResponse(response);

        ResultPacket resultPacket = new ResultPacket();
        resultPacket.getResultsSummary().setTotalMatching(-1);
        response.setResultPacket(resultPacket);
        response.setPagination(new Pagination());

        return searchResponse;
    }
}
