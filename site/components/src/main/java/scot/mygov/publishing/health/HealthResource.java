package scot.mygov.publishing.health;

import com.netflix.hystrix.*;
import scot.gov.publishing.hippo.funnelback.component.HealthEventNotifier;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.netflix.hystrix.HystrixEventType.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/_search/health")
public class HealthResource {

    @Produces(APPLICATION_JSON)
    @GET
    public Response getSitemapIndex() {

        Health health = new Health();
        addCounts(health);
        health.setOk(hasShortCircuitedInLast5Mins());
        health.setLastShortCircuitEvent(SimpleDateFormat.getDateTimeInstance().format(new Date(HealthEventNotifier.getLastShortCurcuitTimestamp())));

        int status =  health.isOk() ? 200 : 503;
        return Response.status(status)
                .entity(health)
                .build();
    }

    boolean hasShortCircuitedInLast5Mins() {
        long timeSinceLastShortCircuit = System.currentTimeMillis() - HealthEventNotifier.getLastShortCurcuitTimestamp();
        return timeSinceLastShortCircuit > TimeUnit.MINUTES.toMillis(5);
    }

    void addCounts(Health health) {

        HystrixCommandKey key = HystrixCommandKey.Factory.asKey("searchWithTimeout");
        HystrixCommandMetrics commandMetrics = HystrixCommandMetrics.getInstance(key);

        if (commandMetrics == null) {
            return;
        }

        Counts counts = new Counts();
        counts.setRollingSuccess(commandMetrics.getRollingCount(SUCCESS));
        counts.setRollingFailure(commandMetrics.getRollingCount(FAILURE));
        counts.setRollingTimeout(commandMetrics.getRollingCount(TIMEOUT));
        counts.setRollingShortCircuitedRequests(commandMetrics.getRollingCount(SHORT_CIRCUITED));
        counts.setCumulativeSuccess(commandMetrics.getCumulativeCount(SUCCESS));
        counts.setCumulativeFailure(commandMetrics.getCumulativeCount(FAILURE));
        counts.setCumulativeTimeout(commandMetrics.getCumulativeCount(TIMEOUT));
        counts.setCumulativeShortCircuitedRequests(commandMetrics.getCumulativeCount(SHORT_CIRCUITED));
        health.setCounts(counts);
    }

    class Health {
        Counts counts = new Counts();

        boolean ok;

        String lastShortCircuitEvent;

        public Counts getCounts() {
            return counts;
        }

        public void setCounts(Counts counts) {
            this.counts = counts;
        }

        public boolean isOk() {
            return ok;
        }

        public void setOk(boolean ok) {
            this.ok = ok;
        }

        public String getLastShortCircuitEvent() {
            return lastShortCircuitEvent;
        }

        public void setLastShortCircuitEvent(String lastShortCircuitEvent) {
            this.lastShortCircuitEvent = lastShortCircuitEvent;
        }

    }

    class Counts {
        long healthErrorCount;
        long healthTotalRequests;

        long rollingSuccess;
        long rollingFailure;
        long rollingTimeout;

        long rollingShortCircuitedRequests;

        long cumulativeSuccess;
        long cumulativeFailure;
        long cumulativeTimeout;
        long cumulativeShortCircuitedRequests;

        public long getHealthErrorCount() {
            return healthErrorCount;
        }

        public void setHealthErrorCount(long healthErrorCount) {
            this.healthErrorCount = healthErrorCount;
        }

        public long getHealthTotalRequests() {
            return healthTotalRequests;
        }

        public void setHealthTotalRequests(long healthTotalRequests) {
            this.healthTotalRequests = healthTotalRequests;
        }

        public long getRollingSuccess() {
            return rollingSuccess;
        }

        public void setRollingSuccess(long rollingSuccess) {
            this.rollingSuccess = rollingSuccess;
        }

        public long getRollingFailure() {
            return rollingFailure;
        }

        public void setRollingFailure(long rollingFailure) {
            this.rollingFailure = rollingFailure;
        }

        public long getRollingTimeout() {
            return rollingTimeout;
        }

        public void setRollingTimeout(long rollingTimeout) {
            this.rollingTimeout = rollingTimeout;
        }

        public long getRollingShortCircuitedRequests() {
            return rollingShortCircuitedRequests;
        }

        public void setRollingShortCircuitedRequests(long rollingShortCircuitedRequests) {
            this.rollingShortCircuitedRequests = rollingShortCircuitedRequests;
        }

        public long getCumulativeSuccess() {
            return cumulativeSuccess;
        }

        public void setCumulativeSuccess(long cumulativeSuccess) {
            this.cumulativeSuccess = cumulativeSuccess;
        }

        public long getCumulativeFailure() {
            return cumulativeFailure;
        }

        public void setCumulativeFailure(long cumulativeFailure) {
            this.cumulativeFailure = cumulativeFailure;
        }

        public long getCumulativeTimeout() {
            return cumulativeTimeout;
        }

        public void setCumulativeTimeout(long cumulativeTimeout) {
            this.cumulativeTimeout = cumulativeTimeout;
        }

        public long getCumulativeShortCircuitedRequests() {
            return cumulativeShortCircuitedRequests;
        }

        public void setCumulativeShortCircuitedRequests(long cumulativeShortCircuitedRequests) {
            this.cumulativeShortCircuitedRequests = cumulativeShortCircuitedRequests;
        }
    }
}