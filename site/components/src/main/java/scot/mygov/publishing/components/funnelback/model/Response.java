package scot.mygov.publishing.components.funnelback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    ResultPacket resultPacket = new ResultPacket();

    CustomData customData = new CustomData();

    public ResultPacket getResultPacket() {
        return resultPacket;
    }

    public void setResultPacket(ResultPacket resultPacket) {
        this.resultPacket = resultPacket;
    }

    public CustomData getCustomData() {
        return customData;
    }

    public void setCustomData(CustomData customData) {
        this.customData = customData;
    }

}
