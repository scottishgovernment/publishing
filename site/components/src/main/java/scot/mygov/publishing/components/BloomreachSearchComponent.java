package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scot.mygov.publishing.components.funnelback.BloomreachSearchService;

import static scot.mygov.publishing.components.SearchComponent.param;

public class BloomreachSearchComponent extends EssentialsContentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(scot.mygov.publishing.components.funnelback.SearchComponent.class);

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        String query = param(request, "q", "");
        int page = getPage(request);
        BloomreachSearchService service = new BloomreachSearchService();
        service.performSearch(query, request, page);
    }

    int getPage(HstRequest request) {
        String pageString = param(request, "page", "1");
        try {
            // the rank used by funnelback is 1 based
            // page is one based
            return Integer.parseInt(pageString);
        } catch (NumberFormatException e) {
            LOG.warn("Invalid page number {}", pageString, e);
            return 1;
        }
    }
}