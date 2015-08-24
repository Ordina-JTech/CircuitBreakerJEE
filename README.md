# CircuitBreakerJEE

A circuit breaker made in JavaEE.

You can use the circuit breaker by simply injecting it:

```
@Inject
private CircuitBreaker circuitBreaker;
```
The circuitbreaker can be used the following way:

`circuitBreaker(SlowSupplier::supplierMethod);`

The circuit breaker can also be injecting with configuration:

```
@Inject
@CircuitBreakerConfig(timeOut = 5000, errorsThreshold = 3, sleepWindow = 5000)
private CircuitBreaker circuitBreaker;
```

The circuitbreaker makes use of a ManagedExecutorService. 

Use this blog to create a ManagedExecutorService in glassfish: <https://blogs.oracle.com/arungupta/entry/create_managedexecutorservice_managedscheduledexecutorservice_managedthreadfactory_contextservice>

A Dockerfile and script is supplied to run the circuit breaker in glassfish 

execute run.sh in the docker directory and watch the running cicuitbreaker example app at http://'DOCKERHOST':8080/CircuitBreakerJEE/