package nl.ordina.reactive.circuitbreaker;

public class CircuitBreakerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CircuitBreakerException(State state) {
		super(state.name());
	}

	public CircuitBreakerException(Throwable ex) {
		super(ex);
	}
}
