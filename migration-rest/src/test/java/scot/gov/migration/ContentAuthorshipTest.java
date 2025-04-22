package scot.gov.migration;

import org.junit.Test;

import jakarta.ws.rs.core.MultivaluedHashMap;

import static java.util.Collections.singletonList;

/**
 * Created by z418868 on 26/11/2020.
 */
public class ContentAuthorshipTest {

    @Test
    public void acceptsValidInput() {
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap();
        map.put("createdBy", singletonList("createdBy"));
        map.put("created", singletonList("2010-01-01 00:00:00"));
        map.put("lastModifiedBy", singletonList("lastModifiedBy"));
        map.put("lastModified", singletonList("2010-01-01 00:00:00"));
        ContentAuthorship.newInstance(map);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidDate() {
        MultivaluedHashMap<String, String> map = new MultivaluedHashMap();
        map.put("createdBy", singletonList("createdBy"));
        map.put("created", singletonList("00"));
        map.put("lastModifiedBy", singletonList("lastModifiedBy"));
        map.put("lastModified", singletonList("2010-01-01 00:00:00"));
        ContentAuthorship.newInstance(map);
    }

}
