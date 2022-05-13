package scot.mygov.publishing.components.funnelback.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomData {

    Stencils stencils;

    public Stencils getStencils() {
        return stencils;
    }

    public void setStencils(Stencils stencils) {
        this.stencils = stencils;
    }
}
