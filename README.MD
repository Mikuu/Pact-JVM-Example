# PACT JVM Example

These codes provide an example about how to do Contract Test with PACT JVM, 
which uses Junit in Consumer side and Gradle task in Provider side, it will cover:

- Microservices examples created with Spring Boot.
- Example of one Provider to two Consumers.
- Write Consumer tests in different ways including using Basic, Junit Rule and DSL method.
- Example of utilizing Pact Broker.

## Understand The Example Applications
Clone the codes to your local, then you can find:

##### Example Provider
This is an API backend service which serves at http://localhost:8080/information, consumers
can retrieve some person information by calling this endpoint with a **query** parameter,
just like below:


##### Example Consumer Miku


##### Example Consumer Nanoha



## Contract Test between Provider and Consumer Miku

## Contract Test between Provider and Consumer Nanoha