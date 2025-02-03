package scot.mygov.publishing.components;

import org.apache.commons.lang3.StringUtils;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.jetbrains.annotations.Nullable;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import scot.mygov.publishing.beans.Image;
import scot.mygov.publishing.beans.News;
import scot.mygov.publishing.channels.WebsiteInfo;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static scot.mygov.publishing.components.FilteredResultsComponent.topicsMap;

public class NewsComponent extends EssentialsContentComponent  {

    @Override
    public @Nullable String cleanupSearchQuery(String query) {
        return super.cleanupSearchQuery(query);
    }

    public void doBeforeRender(final HstRequest request, final HstResponse response) {
        super.doBeforeRender(request, response);


        addTopics(request);
        // populate the organisation information from WebsiteInfo
        request.setAttribute("image", getImage(request));
        WebsiteInfo websiteInfo = MountUtils.websiteInfo(request);
        request.setAttribute("logo", websiteInfo.getLogo());
        request.setAttribute("logoalttext", websiteInfo.getLogoAltText());
        request.setAttribute("orgurl", orgUrl(request, websiteInfo));
    }

    public static void addTopics(HstRequest request) {

        Map<String, String> topicsMap = topicsMap(request);
        request.setAttribute("topicsMap", topicsMap);

        News news = request.getRequestContext().getContentBean(News.class);
        if (news == null) {
            return;
        }

        // the custom field is not generated in the bean
        String[] topics = news.getMultipleProperty("publishing:topics", new String[0]);
        request.setAttribute("topics", Arrays.stream(topics).filter(StringUtils::isNotBlank).collect(toList()));
    }

    String orgUrl(HstRequest request, WebsiteInfo websiteInfo) {
        if (StringUtils.equals(websiteInfo.getLogo(), "scottisg-government")) {
            return "https://www.gov.scot/";
        }
        // we are using a different logo, so use the url of the current site
        HstRequestContext context = request.getRequestContext();
        HstLink siteHomLink = context.getHstLinkCreator().create(context.getSiteContentBaseBean(), context);
        return siteHomLink.toUrlForm(context, true);
    }

    static HippoBean getImage(HstRequest request) {
        HippoBean contentBean = request.getRequestContext().getContentBean();
        if (contentBean == null) {
            return null;
        }

        Image image = contentBean.getBean("publishing:Image", Image.class);
        if (image == null) {
            return null;
        }

        return image.getImage();
    }
}
