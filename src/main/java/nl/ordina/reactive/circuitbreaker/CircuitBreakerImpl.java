package nl.ordina.reactive.circuitbreaker;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.function.Supplier;
import static nl.ordina.reactive.circuitbreaker.CircuitBreaker.State.*;

/**
 * This class is a circuit breaker implementation meant for the call of supplier
 * that returns T.
 */
public class CircuitBreakerImpl<T> implements CircuitBreaker<T> {

    private static final Logger LOG = Logger.getLogger(CircuitBreakerImpl.class.getName());
    private final ExecutorService executorService;

    // This is the default config which will be used if the CircuitbreakerConfig is not used.
    private int errorsThreshold = 5; // amount of faults that are allowed to happen before the circuitbreaker opens.
    private long sleepWindow = 5000; //milliseconds
    private long timeOut = 1000; //milliseconds

    private int errors = 0; // Number of faults since the last successfull call.
    private long lastErrorTime; // System time of the last error.

    private State state = CLOSED;

    public CircuitBreakerImpl(ExecutorService exs, long timeOut, long sleepWindow, int errorsTreshold) {
        this.executorService = exs;
        this.timeOut = timeOut;
        this.sleepWindow = sleepWindow;
        this.errorsThreshold = errorsTreshold;
    }

    /**
     * Default configuration will be used.
     *
     * @param exs
     */
    public CircuitBreakerImpl(ExecutorService exs) {
        this.executorService = exs;
    }

    /**
     * Return the current state of the circuit breaker.
     *
     * @return
     */
    @Override
    public State getState() {
        return state;
    }

    /**
     * Call the supplier via the circuit breaker.
     *
     * @param supplier
     * @return
     */
    @Override
    public T call(Supplier<T> supplier) {
        if (state == HALFOPEN) {
            throw new CircuitBreakerException(state);
        }
        if (state == OPEN) {
            // Check if the state of the circuit breaker should go to half open.
            if ((System.currentTimeMillis() - lastErrorTime) > sleepWindow) {
                // Continue one time calling the supplier to check if the connections is okay again.
                state = HALFOPEN;
            } else {
                throw new CircuitBreakerException(state);
            }
        }
        // Execute the actual call to the supplier.
        return doCall(supplier);
    }

    // This call will happen if the circuit breaker is in a CLOSED or HALFOPEN state.
    private T doCall(Supplier<T> supplier) throws CircuitBreakerException {
        try {
            LOG.log(Level.INFO, "calling supplier");
            Future<T> task = executorService.submit(supplier::get);
            try {
                T result = task.get(timeOut, TimeUnit.MILLISECONDS);
                reset(); // Reset the circuit breaker in case the supplier call is successfull.
                return result;
            } catch (InterruptedException | ExecutionException ex) {
                throw onError(ex.getCause());
            } catch (TimeoutException ex) {
                task.cancel(true);
                throw onError(ex);
            }
        } catch (RejectedExecutionException ex) {
            LOG.log(Level.SEVERE, "threadpool full, rejected");
            throw onError(ex);
        }
    }

    private void reset() {
        state = CLOSED;
        errors = 0;
    }

    private CircuitBreakerException onError(Throwable ex) {
        errors++;
        LOG.log(Level.SEVERE, "timout or other error!!, cancelling task");
        LOG.log(Level.INFO, String.format("errors : %s", errors));
        lastErrorTime = System.currentTimeMillis();
        if (state == HALFOPEN || errors >= errorsThreshold) {
            state = OPEN; // state is open , next call will give open circuit breaker exception
        }
        return new CircuitBreakerException(ex);
    }
}
