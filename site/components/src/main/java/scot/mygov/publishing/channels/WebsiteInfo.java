package scot.mygov.publishing.channels;

import org.hippoecm.hst.configuration.channel.ChannelInfo;
import org.hippoecm.hst.core.parameters.DropDownList;
import org.hippoecm.hst.core.parameters.JcrPath;
import org.hippoecm.hst.core.parameters.Parameter;

public interface WebsiteInfo extends ChannelInfo {

    @Parameter(name = "logo", displayName = "Logo", required = true, defaultValue = "scottish-government")
    @DropDownList({"mygov", "scottish-government", "digital-scotland", "disclosure-scotland"})
    String getLogo();

    @Parameter(name = "logoAltText", required = true, displayName = "Logo alt text", defaultValue = "The Scottish Government")
    String getLogoAltText();

    @Parameter(name = "style", required = true, displayName = "Theme")
    @DropDownList(valueListProvider = ChannelColourValueListProvider.class)
    String getStyle();

    @Parameter(name = "feedbackEnabled", required = true, displayName = "Feedback enabled?")
    Boolean isFeedbackEnabled();

    @Parameter(name = "searchEnabled", required = true, defaultValue = "false", displayName = "Search enabled?")
    Boolean isSearchEnabled();

    @Parameter(name = "siteTitle", required = true, displayName = "Site title")
    String getSiteTitle();

    @Parameter(name = "displaySiteTitleInHeader", required = true, defaultValue = "true", displayName = "Display site title in header?")
    Boolean isDisplaySiteTitleInHeader();

    @Parameter(name = "defaultCardImage", required = false)
    @JcrPath(
            pickerConfiguration = "cms-pickers/images",
            isRelative = true,
            pickerSelectableNodeTypes = {"publishing:ColumnImage", "publishing:ImageCard"},
            pickerInitialPath = ""
    )
    String getDefaultCardImage();

}
