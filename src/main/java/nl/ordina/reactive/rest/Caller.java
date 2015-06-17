package nl.ordina.reactive.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import nl.ordina.reactive.circuitbreaker.CircuitBreaker;
import nl.ordina.reactive.extservices.SlowSupplier;

@Path("/caller")
public class Caller {
    
    @Inject
    private CircuitBreaker circuitBreaker;

    @Path("/direct")
    @GET
    public String callDirect() {
        return SlowSupplier.supplierMethod();
    }
    
    @GET
    @Path("/cb")
    public String call() {
        return   circuitBreaker.call(SlowSupplier::supplierMethod);
    }
    
    @GET
    @Path("cb_state")
    public String getCbState() {
        return circuitBreaker.getState().toString();
    }
}