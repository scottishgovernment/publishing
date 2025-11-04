package scot.mygov.publishing.components.eventbrite;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import scot.gov.publishing.health.Health;
import scot.gov.publishing.health.NagiosStatus;

import java.time.Instant;

@Path("/_health/eventbrite")
public class HealthResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Health healthcheck() {
        EventbriteError error = EventbriteStatusTracker.getLastError();
        Instant lastSuccess = EventbriteStatusTracker.getLastSuccess();
        Health health = new Health();
        health.setStatus(status(error));
        health.setPerformanceData(performanceData(error, lastSuccess));
        health.setMessage(health.getStatus() == NagiosStatus.OK ? lastSucessString(lastSuccess) : error.lastError());
        return health;
    }

    NagiosStatus status(EventbriteError error) {
        if (error != null && isRecent(error)) {
            return NagiosStatus.CRITICAL;
        }
        return NagiosStatus.OK;
    }

    boolean isRecent(EventbriteError error) {
        return !error.timestamp().isBefore(Instant.now().minusSeconds(5 * 60));
    }


    String performanceData(EventbriteError error, Instant lastSuccess) {
        return String.format("%s, last error: %s",
                lastSucessString(lastSuccess),
                error == null? "never used" : error.timestamp());
    }

    String lastSucessString(Instant lastSuccess) {
        return "last success: " + (lastSuccess == null ? "never used" : lastSuccess.toString());
    }
}
