package nl.ordina.reactive.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import nl.ordina.reactive.extservices.SlowSupplier;

@Path("/supplier")
public class SupplierSleep {

    @GET
    public String setSleep(@QueryParam("sleep") long sleep) {
        if (sleep != 0) {
            SlowSupplier.sleep = sleep;
            return "OK, sleep:" + sleep;
        }
        return "You can set the sleep time(milliseconds) of the slow supplier by attaching the query parom 'sleep', for example: 'resources/supplier?sleep=20000'."
                + "<br>Current sleep = " + SlowSupplier.sleep + ".";
    }
}