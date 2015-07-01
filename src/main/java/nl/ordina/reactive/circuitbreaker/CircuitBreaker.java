package nl.ordina.reactive.circuitbreaker;

import java.util.function.Supplier;

public interface CircuitBreaker<T> {

    public enum State {
        OPEN, CLOSED, HALFOPEN
    };

    public T call(Supplier<T> supplier);

    public State getState();
}
