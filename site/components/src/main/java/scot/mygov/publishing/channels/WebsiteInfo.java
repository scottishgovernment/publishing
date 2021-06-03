package scot.mygov.publishing.channels;

import org.hippoecm.hst.configuration.channel.ChannelInfo;
import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.Parameter;

public interface WebsiteInfo extends ChannelInfo {

    @Parameter(name = "style", required = true, displayName = "Style")
    @DropDownList({"mygov", "tradingnation"})
    String getStyle();

    @Parameter(name = "feedbackEnabled", required = true, displayName = "Feedback enabled?")
    Boolean isFeedbackEnabled();

    @Parameter(name = "simpleAnalyticsEnabled", required = true, displayName = "SimpleAnalytics enabled?")
    Boolean isSimpleAnalytivcsEnabled();

    @Parameter(name = "siteTitle", required = true, displayName = "Site title")
    String getSiteTitle();
}
