package scot.mygov.publishing.components.funnelback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {

    ResultPacket resultPacket = new ResultPacket();

    Pagination pagination = new Pagination();

    Curator curator = new Curator();

    public ResultPacket getResultPacket() {
        return resultPacket;
    }

    public void setResultPacket(ResultPacket resultPacket) {
        this.resultPacket = resultPacket;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public Curator getCurator() {
        return curator;
    }

    public void setCurator(Curator curator) {
        this.curator = curator;
    }
}