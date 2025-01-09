package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.*;

public interface LatestNewsInfo {

    @Parameter(name = "count", displayName = "Number of cards to display")
    @DropDownList(valueListProvider = LatestCountValueListProvider.class)
    String getCount();
}