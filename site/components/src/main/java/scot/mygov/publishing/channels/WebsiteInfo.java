package scot.mygov.publishing.channels;

import org.hippoecm.hst.configuration.channel.ChannelInfo;
import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

public interface WebsiteInfo extends ChannelInfo {

    @Parameter(name = "style", required = true, displayName = "Style")
    @DropDownList({"mygov", "tradingnation"})
    String getStyle();

    @Parameter(name = "feedbackEnabled", required = true, displayName = "Feedback enabled?")
    Boolean isFeedbackEnabled();

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
            pickerSelectableNodeTypes = {"publishing:ImageCard"},
            pickerInitialPath = ""
    )
    String getDefaultCardImage();
}
