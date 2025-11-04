package scot.mygov.publishing.components.eventbrite.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DateBlock {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH);

    String timezone;

    String local;

    String utc;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getUtc() {
        return utc;
    }

    public void setUtc(String utc) {
        this.utc = utc;
    }

    public String getDateTime() {
        LocalDateTime localDateTime = LocalDateTime.parse(local);
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.of(timezone));
        return zdt.format(DATE_FORMAT);
    }
}
