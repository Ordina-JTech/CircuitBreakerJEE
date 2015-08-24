#!/bin/bash  
docker build -t circuitbreaker .
docker run -p 8080:8080 -it --name="circuitbreaker"  circuitbreaker 