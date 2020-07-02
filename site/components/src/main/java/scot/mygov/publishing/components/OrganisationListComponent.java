package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.essentials.components.EssentialsContentComponent;
import scot.mygov.publishing.beans.LetterAndOrganisations;
import scot.mygov.publishing.beans.Organisation;
import scot.mygov.publishing.beans.Sector;
import scot.mygov.publishing.beans.SectorAngOrganisations;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class OrganisationListComponent extends EssentialsContentComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) {
        super.doBeforeRender(request, response);
        HippoFolder orgsFolder = folder(request, "organisations");
        HippoFolder sectorsFolder = folder(request, "site-furniture/sectors");
        request.setAttribute("orgsByLetter", getLetterGroups(orgsFolder));
        request.setAttribute("orgsBySector", getSectorGroups(orgsFolder, sectorsFolder));
    }

    /**
     * Map all orgs by their first letter
     */
    static List<LetterAndOrganisations> getLetterGroups( HippoFolder folder) {
        return mapOrgs(folder, OrganisationListComponent::firstLetter)
                .entrySet()
                .stream()
                .map(OrganisationListComponent::letterAndOrganisations)
                .collect(toList());
    }

    static LetterAndOrganisations letterAndOrganisations(Map.Entry<String, List<Organisation>> entry) {
        LetterAndOrganisations letterAndOrganisations = new LetterAndOrganisations();
        letterAndOrganisations.setLetter(entry.getKey());
        letterAndOrganisations.setOrganisations(entry.getValue());
        return letterAndOrganisations;
    }

    static String firstLetter(Organisation organisation) {
        return organisation.getTitle().substring(0, 1).toUpperCase();
    }

    /**
     * Map all orgs by the sector the belong to
     */
    static List<SectorAngOrganisations> getSectorGroups(HippoFolder orgsFolder, HippoFolder sectorsFolder) {
        Map<String, List<Organisation>> bySector = mapOrgs(orgsFolder, Organisation::getSector);
        return sectorsFolder.getDocuments(Sector.class)
                .stream()
                .map(sector -> sectorAngOrganisations(sector, bySector.get(sector.getName())))
                .collect(Collectors.toList());
    }

    static SectorAngOrganisations sectorAngOrganisations(Sector sector, List<Organisation> orgs) {
        SectorAngOrganisations sectorAngOrganisations = new SectorAngOrganisations();
        sectorAngOrganisations.setSector(sector);
        sectorAngOrganisations.setOrganisations(orgs);
        return sectorAngOrganisations;
    }

    static HippoFolder folder(HstRequest request, String path) {
        HippoBean root = request.getRequestContext().getSiteContentBaseBean();
        return root.getBean(path, HippoFolder.class);
    }

    static SortedMap<String, List<Organisation>> mapOrgs(
            HippoFolder folder,
            Function<Organisation, String> keyExtractor) {
        SortedMap<String, List<Organisation>> map = new TreeMap<>();
        List<Organisation> organisations = folder.getDocuments(Organisation.class);
        for (Organisation organisation : organisations) {
            String key = keyExtractor.apply(organisation);
            List<Organisation> organisationsForKey = map.getOrDefault(key, new ArrayList<>());
            organisationsForKey.add(organisation);
            map.putIfAbsent(key, organisationsForKey);
        }
        return map;
    }

}
