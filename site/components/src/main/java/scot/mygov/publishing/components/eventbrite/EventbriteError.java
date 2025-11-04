package scot.mygov.publishing.components.eventbrite;

import java.time.Instant;

public record EventbriteError(String lastError, Instant timestamp) {}
