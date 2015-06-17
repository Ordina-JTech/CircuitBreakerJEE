package nl.ordina.reactive.circuitbreaker;

public class CircuitBreakerException extends RuntimeException {

    public CircuitBreakerException(CircuitBreaker.State state) {
        super(state.name());
    }

    public CircuitBreakerException(Throwable ex) {
        super(ex);
    }
}
