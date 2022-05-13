package scot.mygov.publishing.components.funnelback.postprocessing;

import org.apache.commons.lang3.StringUtils;
import scot.mygov.publishing.components.funnelback.model.FunnelbackSearchResponse;
import scot.mygov.publishing.components.funnelback.model.Page;
import scot.mygov.publishing.components.funnelback.model.Pagination;

/**
 * Created by z418868 on 17/05/2022.
 */
public class PaginationProcessor implements PostProcessor {

    @Override
    public void process(FunnelbackSearchResponse response) {
        Pagination pagination = response.getResponse().getCustomData().getStencils().getPagination();
        rewritexPagination(pagination);
    }

    void rewritexPagination(Pagination pagination) {
        rewrite(pagination.getFirst());
        rewrite(pagination.getLast());
        rewrite(pagination.getPrevious());
        pagination.getPages().stream().forEach(this::rewrite);
    }

    void rewrite(Page page) {
        if (page == null) {
            return;
        }

        String [] params = StringUtils.split(page.getUrl().substring(1), '&');
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            if (StringUtils.startsWith(param, "start_rank=")) {
                replaceStartRankWithPage(page, param);
            }
        }
    }

    void replaceStartRankWithPage(Page page, String param) {
        String [] nameAndVal = StringUtils.split(param, '=');
        String val = nameAndVal[1];
        int rank = Integer.parseInt(val);
        int pageNumber = ((rank - 1) / 10) + 1;
        String url = StringUtils.replace(page.getUrl(), param, "page=" + pageNumber);
        page.setUrl(url);
    }

}
