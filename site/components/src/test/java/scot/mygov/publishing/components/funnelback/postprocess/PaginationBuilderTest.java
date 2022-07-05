package scot.mygov.publishing.components.funnelback.postprocess;

import org.junit.Test;
import scot.gov.publishing.hippo.funnelback.model.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by z418868 on 17/06/2022.
 */
public class PaginationBuilderTest {

    PaginationBuilder sut = new PaginationBuilder("https://www.mygov.scot/search");

    @Test
    public void resultsLessThanPageSize() {

        // ARRANGE
        ResultsSummary resultsSummary = resultsSummary(5, 1);

        // ACT
        Pagination pagination = sut.getPagination(resultsSummary, anyQuery());

        // ASSERT
        assertTrue("no pagination should be present for 5 results", pagination.getPages().isEmpty());
        assertNull(pagination.getFirst());
        assertNull(pagination.getLast());
    }

    @Test
    public void threePagesOfResultsWithCurrentPage1() {
        // ARRANGE
        ResultsSummary resultsSummary = resultsSummary(25, 1);

        // ACT
        Pagination pagination = sut.getPagination(resultsSummary, anyQuery());

        // ASSERT
        assertEquals("wrong number of pages", 4, pagination.getPages().size());
        assertTrue("wrong item selected", pagination.getPages().get(0).isSelected());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=1", pagination.getPages().get(0).getUrl());
        assertNull(pagination.getFirst());
        assertNull(pagination.getLast());
    }

    @Test
    public void tenPagesOfResultsWithCurrentPage5() {
        // ARRANGE
        ResultsSummary resultsSummary = resultsSummary(95, 41);

        // ACT
        Pagination pagination = sut.getPagination(resultsSummary, anyQuery());

        // ASSERT
        assertEquals("wrong number of pages", 3, pagination.getPages().size());
        assertTrue("wrong item selected", pagination.getPages().get(1).isSelected());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=5", pagination.getPages().get(1).getUrl());
        assertEquals(pagination.getFirst().getUrl(), "https://www.mygov.scot/search?q=anyQuery&page=1");
        assertEquals(pagination.getLast().getUrl(), "https://www.mygov.scot/search?q=anyQuery&page=10");
    }

    @Test
    public void tenPagesOfResultsWithCurrentPage3() {
        // ARRANGE
        ResultsSummary resultsSummary = resultsSummary(95, 21);

        // ACT
        Pagination pagination = sut.getPagination(resultsSummary, anyQuery());

        // ASSERT
        assertEquals("wrong number of pages", 4, pagination.getPages().size());
        assertTrue("wrong item selected", pagination.getPages().get(2).isSelected());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=3", pagination.getPages().get(2).getUrl());
        assertNull("first should be null", pagination.getFirst());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=10", pagination.getLast().getUrl());
    }

    @Test
    public void tenPagesOfResultsWithCurrentPage7() {
        // ARRANGE
        ResultsSummary resultsSummary = resultsSummary(95, 71);

        // ACT
        Pagination pagination = sut.getPagination(resultsSummary, anyQuery());

        // ASSERT
        assertEquals("wrong number of pages", 4, pagination.getPages().size());
        assertTrue("wrong item selected", pagination.getPages().get(1).isSelected());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=8", pagination.getPages().get(1).getUrl());
        assertNull("last should be null", pagination.getLast());
        assertEquals("https://www.mygov.scot/search?q=anyQuery&page=1", pagination.getFirst().getUrl());
    }

    String anyQuery() {
        return "anyQuery";
    }

    ResultPacket resultPacket(int matches) {
        ResultPacket resultPacket = new ResultPacket();
        //resultPacket.setResults(results(matches));
        resultPacket.setResultsSummary(summary(matches));
        return resultPacket;
    }

    ResultsSummary summary(int totalMatching) {
        ResultsSummary summary = new ResultsSummary();
        summary.setTotalMatching(totalMatching);
        summary.setNumRanks(10);
        return summary;
    }

    ResultsSummary resultsSummary(int matches, int start) {
        ResultsSummary resultsSummary = new ResultsSummary();
        resultsSummary.setTotalMatching(matches);
        resultsSummary.setCurrStart(start);
        resultsSummary.setNumRanks(10);
        return resultsSummary;
    }

}
