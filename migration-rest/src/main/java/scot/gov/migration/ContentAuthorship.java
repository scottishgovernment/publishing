package scot.gov.migration;

import jakarta.ws.rs.core.MultivaluedMap;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ContentAuthorship {

    private String createdBy;

    private Calendar createdDate;

    private String modifiedBy;

    private Calendar modifiedDate;

    ContentAuthorship(String createdBy, Calendar createdDate, String modifiedBy, Calendar modifiedDate) {
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.modifiedBy = modifiedBy;
        this.modifiedDate = modifiedDate;
    }

    static ContentAuthorship newInstance(MultivaluedMap<String, String> queryParams) {

        String createdBy = queryParams.getFirst("createdBy");
        String createdDate = queryParams.getFirst("created");
        String modifiedBy = queryParams.getFirst("lastModifiedBy");
        String modifiedDate = queryParams.getFirst("lastModified");

        return new ContentAuthorship(
                createdBy,
                parseDate(createdDate),
                modifiedBy,
                parseDate(modifiedDate));

    }

    static Calendar parseDate(String str) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFormat.parse(str));
            return cal;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Illegal date", e);
        }
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Calendar getCreatedDate() {
        return createdDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Calendar getModifiedDate() {
        return modifiedDate;
    }
}
