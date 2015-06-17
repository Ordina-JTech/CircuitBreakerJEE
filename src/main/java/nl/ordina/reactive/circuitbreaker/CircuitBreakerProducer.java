package nl.ordina.reactive.circuitbreaker;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class CircuitBreakerProducer {

    @Resource(lookup = "concurrent/myExecutor")
    private ManagedExecutorService exs;
    
    private static final Map<String, CircuitBreaker> circuitBreakers = new HashMap<>();

    @Produces
    public CircuitBreaker createCircuitPreaker(InjectionPoint injectionPoint) {
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
