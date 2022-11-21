package scot.mygov.publishing.channels;

import org.hippoecm.hst.configuration.channel.ChannelInfo;
import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

public interface WebsiteInfo extends ChannelInfo {

    @Parameter(name = "logo", displayName = "Logo (campaign sites only)")
    @JcrPath(
        pickerConfiguration = "cms-pickers/images",
        pickerSelectableNodeTypes = {"hippogallery:imageset"},
        pickerInitialPath = "/content/gallery"
    )
    String getLogoPath();

    @Parameter(name = "style", required = true, displayName = "Style")
    @DropDownList({"mygov", "tradingnation", "campaign"})
    String getStyle();

    @Parameter(name = "feedbackEnabled", required = true, displayName = "Feedback enabled?")
    Boolean isFeedbackEnabled();

    @Parameter(name = "searchEnabled", required = true, defaultValue = "false", displayName = "Search enabled?")
    Boolean isSearchEnabled();

    @Parameter(name = "simpleAnalyticsEnabled", required = true, displayName = "SimpleAnalytics enabled?")
    Boolean isSimpleAnalyticsEnabled();

    @Parameter(name = "siteTitle", required = true, displayName = "Site title")
    String getSiteTitle();

    @Parameter(name = "color1", displayName = "Additional colour 1")
    @DropDownList(valueListProvider = ChannelColourValueListProvider.class)
    String getColor1();

    @Parameter(name = "color2", displayName = "Additional colour 2")
    @DropDownList(valueListProvider = ChannelColourValueListProvider.class)
    String getColor2();

    @Parameter(name = "defaultCardImage", required = false)
    @JcrPath(
            pickerConfiguration = "cms-pickers/images",
            isRelative = true,
            pickerSelectableNodeTypes = {"publishing:ColumnImage"},
            pickerInitialPath = ""
    )
    String getDefaultCardImage();

}
