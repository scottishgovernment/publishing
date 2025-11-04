package scot.mygov.publishing.components.eventbrite;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public class EventbriteStatusTracker {

    private static final AtomicReference<EventbriteError> lastError = new AtomicReference<>();
    private static final AtomicReference<Instant> lastSuccess = new AtomicReference<>();

    private EventbriteStatusTracker() {
        // hide default constructor
    }

    public static void recordError(Throwable cause) {
        lastError.set(new EventbriteError(cause.getCause().getMessage(), Instant.now()));
    }

    public static void recordSuccess() {
        lastSuccess.set(Instant.now());
    }

    public static EventbriteError getLastError() {
        return lastError.get();
    }

    public static Instant getLastSuccess() {
        return lastSuccess.get();
    }
}