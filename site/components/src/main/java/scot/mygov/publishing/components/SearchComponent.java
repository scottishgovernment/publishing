package scot.mygov.publishing.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.ComponentConfiguration;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;

import javax.servlet.ServletContext;

import static scot.mygov.publishing.components.SiteHeaderComponent.populateAutoCompleteFlag;

public class SearchComponent extends EssentialsContentComponent {

    private String searchType;

    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) {
        super.init(servletContext, componentConfig);
        this.searchType = componentConfig.getRawParameters().getOrDefault("searchtype", "resilient");
    }

    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);

        // each page has a designated search type, and then there are the search settings for the site.
        // the specific funnelback and bloomreach pages are for testing and sholdhave their auto complete
        // value hard coded.
        //
        // otehrwise the value is determined from the search settings document
        switch (searchType) {
            case "bloomreach":
                request.setAttribute("autoCompleteEnabled", false);
                break;
            case "funnelback":
                request.setAttribute("autoCompleteEnabled", true);
                break;
            default:
                // defer to the type specific in search settings
                populateAutoCompleteFlag(request);
        }
    }

}
