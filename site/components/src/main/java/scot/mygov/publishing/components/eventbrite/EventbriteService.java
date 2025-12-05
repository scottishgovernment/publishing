package scot.mygov.publishing.components.eventbrite;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.stereotype.Service;

@Service
public class EventbriteService {
    CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            // % of failures to open circuit
            .failureRateThreshold(50)

            // use number of requests rather than time
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)

            // last N calls are used to calculate the percentage, since we cache for 15 mins I think this should be fairly low
            .slidingWindowSize(6)

            // the default for this is 100 ... we will be making low number of requests and so this needs to be low
            .minimumNumberOfCalls(6)

            // how long to go from open to half open
            // so if we get a failure it will wait a minute before going to half open
            .waitDurationInOpenState(java.time.Duration.ofSeconds(60))

            // How many “test” calls in HALF_OPEN
            .permittedNumberOfCallsInHalfOpenState(1)

            .build();

    CircuitBreaker circuitBreaker =
            CircuitBreaker.of("eventbriteCircuitBreaker", config);


}
