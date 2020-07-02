package scot.mygov.publishing.components;

import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.junit.Test;
import scot.mygov.publishing.beans.LetterAndOrganisations;
import scot.mygov.publishing.beans.Organisation;
import scot.mygov.publishing.beans.Sector;
import scot.mygov.publishing.beans.SectorAngOrganisations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by z418868 on 02/07/2020.
 */
public class OrganisationListComponentTest {

    @Test
    public void getLetterGroupsAsExected() {

        // ARRANGE
        List<Organisation> organisations = new ArrayList<>();
        Organisation zebras = org("Zebras");
        Organisation aardvarks = org("aardvarks");
        Organisation apes = org("Apes");
        Collections.addAll(organisations, zebras, aardvarks, apes);
        HippoFolder folder = folderWithOrgs(organisations);

        // ACT
        List<LetterAndOrganisations> actual = OrganisationListComponent.getLetterGroups(folder);

        // ASSERT
        assertTrue(actual.size() == 2);
        assertSame(actual.get(0).getOrganisations().get(0), aardvarks);
        assertSame(actual.get(0).getOrganisations().get(1), apes);
        assertSame(actual.get(1).getOrganisations().get(0), zebras);
    }

    @Test
    public void getSectorGroups() {

        // ARRANGE
        List<Organisation> organisations = new ArrayList<>();
        Organisation zebras = org("Zebras", "equine");
        Organisation horses = org("Horse", "equine");
        Organisation gorilas = org("Gorilla", "apes");
        Collections.addAll(organisations, zebras, horses, gorilas);

        List<Sector> sectors = new ArrayList<>();
        Sector equine = sector("equine");
        Sector apes = sector("apes");
        Collections.addAll(sectors, apes, equine);

        HippoFolder orgFolder = folderWithOrgs(organisations);
        HippoFolder sectorsFolder = folderWithSectors(sectors);

        // ACT
        List<SectorAngOrganisations> actual = OrganisationListComponent.getSectorGroups(orgFolder, sectorsFolder);

        // ASSERT
        assertTrue(actual.size() == 2);
        assertSame(actual.get(0).getSector(), apes);
        assertSame(actual.get(1).getSector(), equine);
    }


    Organisation org(String title) {
        return org(title, "sector");
    }

    Organisation org(String title, String sector) {
        Organisation organisation = mock(Organisation.class);
        when(organisation.getTitle()).thenReturn(title);
        when(organisation.getSector()).thenReturn(sector);
        return organisation;
    }

    Sector sector(String name) {
        Sector sector = mock(Sector.class);
        when(sector.getName()).thenReturn(name);
        return sector;
    }
    HippoFolder folderWithOrgs(List<Organisation> organisations) {
        HippoFolder folder = mock(HippoFolder.class);
        when(folder.getDocuments(Organisation.class)).thenReturn(organisations);
        return folder;
    }

    HippoFolder folderWithSectors(List<Sector> sectors) {
        HippoFolder folder = mock(HippoFolder.class);
        when(folder.getDocuments(Sector.class)).thenReturn(sectors);
        return folder;
    }
}
