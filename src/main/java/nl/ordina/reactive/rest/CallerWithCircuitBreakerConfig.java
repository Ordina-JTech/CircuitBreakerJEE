package nl.ordina.reactive.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import nl.ordina.reactive.circuitbreaker.CircuitBreaker;
import nl.ordina.reactive.circuitbreaker.CircuitBreakerConfig;
import nl.ordina.reactive.circuitbreaker.State;
import nl.ordina.reactive.extservices.SlowSupplier;

@Path("/callerAdvanced")
public class CallerWithCircuitBreakerConfig {

    @Inject
    @CircuitBreakerConfig(timeOut = 5000, errorsThreshold = 3, sleepWindow = 5000)
    private CircuitBreaker<String> configuredCircuitBreaker;

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

    @POST
    @Path("cb_state")
    @Consumes(MediaType.TEXT_PLAIN)
    public void setCbState(String state) {
        configuredCircuitBreaker.setState(State.valueOf(state.trim()));
    }
}
