package nl.ordina.reactive.circuitbreaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class CircuitBreakerProducer {

    @Resource(lookup = "concurrent/myExecutor")
    private ManagedExecutorService exs;

    //This is not ideal, CircuitBreaker may be created twice but the last created CircuitBreaker will be used.
    private static final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

    @Produces
    public <T> CircuitBreaker<T> createCircuitPreaker(InjectionPoint injectionPoint) {
        String key = injectionPoint.getBean().getBeanClass().getName();
        CircuitBreaker cb = circuitBreakers.get(key);
        if (cb != null) {
            return cb;
        }
        CircuitBreakerConfig config = injectionPoint.getAnnotated().getAnnotation(CircuitBreakerConfig.class);
        if (config == null) {
            cb = new CircuitBreakerImpl(exs);
        } else {
            cb = new CircuitBreakerImpl(exs, config.timeOut(), config.sleepWindow(), config.errorsThreshold());
        }
        circuitBreakers.put(key, cb);
        return cb;
    }
}
