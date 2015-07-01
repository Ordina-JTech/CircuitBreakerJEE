package nl.ordina.reactive.circuitbreaker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.Assert.*;
import org.junit.Test;

public class CircuitBreakerImplTest {

    private final ExecutorService ex = Executors.newCachedThreadPool();

    public static class TestSupplier {

        public static String testCallException() {
            throw new RuntimeException("testcallex");
        }

        public static String testCall() {
            return "testCall";
        }
    }

    @Test
    public void normalOperation() {
        CircuitBreaker<String> cb = new CircuitBreakerImpl(ex, 500, 500, 3);
        assertEquals(State.CLOSED, cb.getState()); // default state is closed
        assertEquals("testCall", cb.call(TestSupplier::testCall));
        assertEquals(State.CLOSED, cb.getState()); // stay closed

    }

    @Test
    public void testErrorCount() {
        CircuitBreaker<String> cb = new CircuitBreakerImpl(ex, 500, 500, 3);
        assertEquals("testcallex", CallTestMethodWithException(cb).getCause().getMessage());
        assertEquals("testcallex", CallTestMethodWithException(cb).getCause().getMessage());
        assertEquals("testcallex", CallTestMethodWithException(cb).getCause().getMessage());
        // 4th call should return open cb  (repeatedly ) 
        // cb should be open
        assertEquals("OPEN", CallTestMethodWithException(cb).getMessage());
        assertEquals("OPEN", CallTestMethodWithException(cb).getMessage());

    }

    @Test
    public void testSleepWindow() throws InterruptedException {
        CircuitBreaker<String> cb = new CircuitBreakerImpl(ex, 500, 500, 3);
        assertEquals("testcallex", CallTestMethodWithException(cb).getCause().getMessage());
        assertEquals("testcallex", CallTestMethodWithException(cb).getCause().getMessage());
        assertEquals("testcallex", CallTestMethodWithException(cb).getCause().getMessage());
        // 4th call should return open cb  (repeatedly ) 
        assertEquals("OPEN", CallTestMethodWithException(cb).getMessage());
        // then sleep for 1 second ( > 0,5 second) 
        Thread.sleep(1000);
        // cb halfopen, should be closed again after normal cal
        assertEquals("testCall", cb.call(TestSupplier::testCall));
        assertEquals(State.CLOSED, cb.getState());

    }

    @Test
    public void testSleepWindowFailingCall() throws InterruptedException {
        CircuitBreaker<String> cb = new CircuitBreakerImpl(ex, 500, 500, 3);
        assertEquals("testcallex", CallTestMethodWithException(cb).getCause().getMessage());
        assertEquals("testcallex", CallTestMethodWithException(cb).getCause().getMessage());
        assertEquals("testcallex", CallTestMethodWithException(cb).getCause().getMessage());
        // 4th call should return open cb  (repeatedly ) 
        assertEquals("OPEN", CallTestMethodWithException(cb).getMessage());
        // then sleep for 1 second ( > 0,5 second) 
        Thread.sleep(1000);
        // cb should be still open again after execption cal
        assertEquals("testcallex", CallTestMethodWithException(cb).getCause().getMessage());
        assertEquals(State.OPEN, cb.getState());

    }

    private Exception CallTestMethodWithException(CircuitBreaker<String> cb) {
        try {
            cb.call(TestSupplier::testCallException);
        } catch (CircuitBreakerException e) {
            return e;
        }
        return null;
    }
}
