package scot.mygov.publishing.components.funnelback.postprocess;

import org.junit.Assert;
import org.junit.Test;
import scot.mygov.publishing.components.funnelback.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by z418868 on 17/06/2022.
 */
public class PaginationProcessorTest {

    PaginationProcessor sut = new PaginationProcessor("https://www.mygov.scot/search");

    @Test
    public void resultsLessThanPageSize() {

        // ARRANGE
        FunnelbackSearchResponse response = new FunnelbackSearchResponse();
        response.getResponse().setResultPacket(resultPacket(5));
        response.getResponse().getResultPacket().getResultsSummary().setCurrStart(1);

        // ACT
        sut.process(response);

        // ASSERT
        Pagination pagination = response.getResponse().getPagination();
        assertTrue("no pagination should be present for 5 results", pagination.getPages().isEmpty());
        assertNull(pagination.getFirst());
        assertNull(pagination.getLast());
    }

    @Test
    public void fivePagesOfResultsWithCurrentPage1() {
        // ARRANGE
        FunnelbackSearchResponse response = new FunnelbackSearchResponse();
        response.getQuestion().setOriginalQuery(anyQuery());
        response.getResponse().setResultPacket(resultPacket(45));
        response.getResponse().getResultPacket().getResultsSummary().setCurrStart(1);

        // ACT
        sut.process(response);

        // ASSERT
        Pagination pagination = response.getResponse().getPagination();
        assertEquals("wrong number of pages", 5, pagination.getPages().size());
        assertTrue("wrong item selected", pagination.getPages().get(0).isSelected());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=1", pagination.getPages().get(0).getUrl());
        assertNull(pagination.getFirst());
        assertNull(pagination.getLast());
    }

    @Test
    public void tenPagesOfResultsWithCurrentPage5() {
        // ARRANGE
        FunnelbackSearchResponse response = new FunnelbackSearchResponse();
        response.getQuestion().setOriginalQuery(anyQuery());
        response.getResponse().setResultPacket(resultPacket(95));
        response.getResponse().getResultPacket().getResultsSummary().setCurrStart(41);

        // ACT
        sut.process(response);

        // ASSERT
        Pagination pagination = response.getResponse().getPagination();
        assertEquals("wrong number of pages", 5, pagination.getPages().size());
        assertTrue("wrong item selected", pagination.getPages().get(2).isSelected());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=5", pagination.getPages().get(2).getUrl());

        assertEquals(pagination.getFirst().getUrl(), "https://www.mygov.scot/search?q=anyQuery&page=1");
        assertEquals(pagination.getLast().getUrl(), "https://www.mygov.scot/search?q=anyQuery&page=10");
    }

    @Test
    public void tenPagesOfResultsWithCurrentPage4() {
        // ARRANGE
        FunnelbackSearchResponse response = new FunnelbackSearchResponse();
        response.getQuestion().setOriginalQuery(anyQuery());
        response.getResponse().setResultPacket(resultPacket(95));
        response.getResponse().getResultPacket().getResultsSummary().setCurrStart(31);

        // ACT
        sut.process(response);

        // ASSERT
        Pagination pagination = response.getResponse().getPagination();
        assertEquals("wrong number of pages", 5, pagination.getPages().size());
        assertTrue("wrong item selected", pagination.getPages().get(3).isSelected());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=4", pagination.getPages().get(3).getUrl());
        assertNull("first should be null", pagination.getFirst());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=10", pagination.getLast().getUrl());
    }

    @Test
    public void tenPagesOfResultsWithCurrentPage6() {
        // ARRANGE
        FunnelbackSearchResponse response = new FunnelbackSearchResponse();
        response.getQuestion().setOriginalQuery(anyQuery());
        response.getResponse().setResultPacket(resultPacket(95));
        response.getResponse().getResultPacket().getResultsSummary().setCurrStart(61);

        // ACT
        sut.process(response);

        // ASSERT
        Pagination pagination = response.getResponse().getPagination();
        assertEquals("wrong number of pages", 6, pagination.getPages().size());
        assertTrue("wrong item selected", pagination.getPages().get(2).isSelected());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=7", pagination.getPages().get(2).getUrl());
        assertNull("last should be null", pagination.getLast());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=1", pagination.getFirst().getUrl());
    }

    String anyQuery() {
        return "anyQuery";
    }

    ResultPacket resultPacket(int matches) {
        ResultPacket resultPacket = new ResultPacket();
        resultPacket.setResults(results(matches));
        resultPacket.setResultsSummary(summary(matches));
        return resultPacket;
    }

    ResultsSummary summary(int totalMatching) {
        ResultsSummary summary = new ResultsSummary();
        summary.setTotalMatching(totalMatching);
        summary.setNumRanks(10);
        return summary;
    }

    List<Result> results(int count) {
        List<Result> results = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            results.add(anyResult());
        }
        return results;
    }

    Result anyResult() {
        Result result = new Result();
        result.setLiveUrl(anyUrl());
        result.setTitle(anyTitle());
        return result;
    }

    String anyTitle() {
        return "title";
    }

    String anyUrl() {
        return "https://www.mygov.scot/url";
    }
}
