package scot.mygov.publishing.beans;

import java.util.ArrayList;
import java.util.List;

public class SectorAngOrganisations {

    Sector sector;

    List<Organisation> organisations = new ArrayList<>();

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public List<Organisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<Organisation> organisations) {
        this.organisations = organisations;
    }
}
