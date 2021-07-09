package scot.mygov.publishing.components;

import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.cms7.essentials.components.EssentialsDocumentComponent;

@ParametersInfo(type = ImageAndTextComponentInfo.class)
public class ImageAndTextComponent extends EssentialsDocumentComponent {

    // nothing required - the document will beavailable in the rewuest, and the other options from the params info
    // as 'cparam', for example cparam.theme

}
