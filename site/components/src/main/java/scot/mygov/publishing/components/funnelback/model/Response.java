package scot.mygov.publishing.components.funnelback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    ResultPacket resultPacket = new ResultPacket();

    CustomData customData = new CustomData();

    HippoBeanIterator test;
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

    public HippoBeanIterator getTest() {
        return test;
    }

    public void setTest(HippoBeanIterator test) {
        this.test = test;
    }
}
