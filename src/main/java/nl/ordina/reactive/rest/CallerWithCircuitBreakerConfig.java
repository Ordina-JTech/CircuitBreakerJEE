package nl.ordina.reactive.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import nl.ordina.reactive.circuitbreaker.CircuitBreaker;
import nl.ordina.reactive.circuitbreaker.CircuitBreakerConfig;
import nl.ordina.reactive.extservices.SlowSupplier;

@Path("/callerAdvanced")
public class CallerWithCircuitBreakerConfig {

    @Inject
    @CircuitBreakerConfig(timeOut = 5000, errorsThreshold = 3, sleepWindow = 5000)
    private CircuitBreaker configuredCircuitBreaker;

    @GET
    @Path("/cb")
    public String call() {
        return configuredCircuitBreaker.call(SlowSupplier::supplierMethod);
    }

    @GET
    @Path("cb_state")
    public String getCbState() {
        return configuredCircuitBreaker.getState().toString();
    }
}
