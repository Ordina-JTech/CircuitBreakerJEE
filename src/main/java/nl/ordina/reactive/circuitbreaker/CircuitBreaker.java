package nl.ordina.reactive.circuitbreaker;

import java.util.function.Supplier;

public interface CircuitBreaker<T> {


    public T call(Supplier<T> supplier);

    public State getState();
    
    public void setState(State state);
}
