package scot.mygov.publishing.components.funnelback.postprocess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.components.funnelback.model.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

public class PaginationProcessor implements PostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PaginationProcessor.class);

    private static final int PAGES = 5;

    String baseUrl;

    public PaginationProcessor(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void process(FunnelbackSearchResponse response) {

        ResultsSummary resultsSummary = response.getResponse().getResultPacket().getResultsSummary();
        if (resultsSummary.getTotalMatching() <= resultsSummary.getNumRanks()) {
            return;
        }

        Pagination pagination = response.getResponse().getPagination();
        String query = response.getQuestion().getOriginalQuery();
        int currentPage = rankToPage(resultsSummary.getCurrStart(), resultsSummary.getNumRanks());
        pagination.setCurrentPageIndex(currentPage);
        int maxPage = rankToPage(resultsSummary.getTotalMatching(), resultsSummary.getNumRanks());

        int firstPage = firstPage(currentPage, maxPage);
        int lastPage = lastPage(firstPage, maxPage);
        Set<Integer> included = new HashSet<>();
        for (int pageIndex = firstPage; pageIndex <= lastPage; pageIndex++) {
            Page page = page(baseUrl, query, pageIndex);
            included.add(pageIndex);
            page.setSelected(pageIndex == currentPage);
            pagination.getPages().add(page);
        }

        if (!included.contains(1)) {
            Page first = page(baseUrl, query, 1);
            pagination.setFirst(first);
        }

        if (!included.contains(maxPage)) {
            Page last = page(baseUrl, query, maxPage);
            pagination.setLast(last);
        }

        if (currentPage != 1) {
            Page previous = page(baseUrl, query, currentPage - 1);
            previous.setLabel("Previous");
            pagination.setPrevious(previous);
        }

        if (currentPage != maxPage) {
            Page next = page(baseUrl, query, currentPage + 1);
            next.setLabel("Next");
            pagination.setNext(next);
        }
    }

    int rankToPage(int rank, int pageSize) {
        return (rank - 1) / pageSize + 1;
    }

    int firstPage(int currentPage, int maxPage) {
        int minBeforeCount = PAGES / 2;

        if (currentPage <= minBeforeCount) {
            return 1;
        }

        if (currentPage + minBeforeCount >= maxPage) {
            return Math.max(1, maxPage - PAGES + 1);
        }

        // if the first page is 2 then juts make it 1
        int page = currentPage - minBeforeCount;
        return page == 2 ? 1 : page;
    }

    int lastPage(int firstPage, int maxPage) {
        int page = Math.min(firstPage + PAGES - 1, maxPage);

        // if the first page the second to last then make it the last one
        if (page == maxPage - 1) {
            return maxPage;
        }

        return page;
    }

    Page page(String base, String query, int index) {
        Page page = new Page();
        String encodedQuery = encodeParam(query);
        String url = new StringBuffer(base)
                        .append('?')
                        .append("q=")
                        .append(encodedQuery)
                        .append("&page=").append(index)
                        .toString();
        page.setLabel(Integer.toString(index));
        page.setUrl(url);
        page.setSelected(false);
        return page;
    }

    String encodeParam(String param) {
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Failed to encode param: {}", param, e);
            return param;
        }

    }
}
