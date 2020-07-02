package scot.mygov.publishing.beans;

import java.util.ArrayList;
import java.util.List;

public class LetterAndOrganisations {

    String letter;

    List<Organisation> organisations = new ArrayList<>();

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public List<Organisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<Organisation> organisations) {
        this.organisations = organisations;
    }
}
