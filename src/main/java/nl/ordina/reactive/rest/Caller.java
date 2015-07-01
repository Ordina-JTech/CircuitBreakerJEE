package nl.ordina.reactive.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import nl.ordina.reactive.circuitbreaker.CircuitBreaker;
import nl.ordina.reactive.circuitbreaker.State;
import nl.ordina.reactive.extservices.SlowSupplier;

@Path("/caller")
public class Caller {

    @Inject
    private CircuitBreaker<String> circuitBreaker;

    @Path("/direct")
    @GET
    public String callDirect() {
        return SlowSupplier.supplierMethod();
    }

    @GET
    @Path("/cb")
    public String call() {
        return circuitBreaker.call(SlowSupplier::supplierMethod);
    }

    @GET
    @Path("cb_state")
    public String getCbState() {
        return circuitBreaker.getState().toString();
    }

    @POST
    @Path("cb_state")
    @Consumes(MediaType.TEXT_PLAIN)
    public void setCbState(String state) {
        circuitBreaker.setState(State.valueOf(state.trim()));
    }
}
