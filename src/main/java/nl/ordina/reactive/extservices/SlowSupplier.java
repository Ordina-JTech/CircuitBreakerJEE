package nl.ordina.reactive.extservices;

import java.util.logging.Level;
import java.util.logging.Logger;
import nl.ordina.reactive.circuitbreaker.CircuitBreakerImpl;

/*
 * This class is meant to simulate a slow supplier.
 * It will only return a string, and the slowness of the supplier can be set using the supplier rest resource.
 * This resource can be found in the class 'SupplierSleep'.
*/
public class SlowSupplier {
    
    public static long sleep = 50;

    public static String supplierMethod() {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException ex) {
            Logger.getLogger(CircuitBreakerImpl.class.getName()).log(Level.SEVERE, "interrupted", ex);
        }
        return "We are the knights how say Ni, Ni!<br>"
                + "We demand a sacrifice. "
                + "<br><br>We demand a shrubbery!";
    }
}