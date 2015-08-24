#!/bin/bash  
copy ../target/CircuitBreakerWeb.war ./
docker build -t circuitbreaker .
docker run -p 8080:8080 -it --name="circuitbreaker"  circuitbreaker 