# PACT JVM Example

These codes provide an example about how to do Contract Test with PACT JVM, 
which uses Junit in Consumer side and Gradle task in Provider side, it will cover:

- Microservices examples created with Spring Boot.
- Example of one Provider to two Consumers.
- Write Consumer tests in different ways including using Basic, Junit Rule and DSL method.
- Example of utilizing Pact Broker.

## Understand The Example Applications
Clone the codes to your local, then you can find:

### Example Provider
This is an API backend service which serves at http://localhost:8080/information, consumers
can retrieve some person information by calling this endpoint with a query parameter **name**,
just like calling http://localhost:8080/information?name=Miku will get:

![](https://github.com/Mikuu/Pact-JVM-Example/blob/master/screenshot/provider.miku.png)

and calling http://localhost:8080/information?name=Nanoha will get:

![](https://github.com/Mikuu/Pact-JVM-Example/blob/master/screenshot/provider.nanoha.png)

### Example Consumer Miku
This is first example consumer we called [Miku](https://en.wikipedia.org/wiki/Hatsune_Miku), to start it:

`./gradlew :example-consumer-miku:bootRun`

then visit http://localhost:8081/miku in your browser, you can get this:

![](https://github.com/Mikuu/Pact-JVM-Example/blob/master/screenshot/consumer.miku.png)

compare with Provider's payload and the information on the web page, you can find that the attributes `salary` and 
`nationality` are not used by Miku.

### Example Consumer Nanoha
This is the second example consumer we called [Nanoha](http://nanoha.wikia.com/wiki/Nanoha_Takamachi), to start it:

`./gradlew :example-consumer-nanoha:bootRun`

then visit http://localhost:8082/nanoha in your browser, you can get this:

![image](https://github.com/Mikuu/Pact-JVM-Example/blob/master/screenshot/consumer.nanoha.png)

similar to Miku, Nanoha does not use the attribute `salary` neither but uses attribute `nationality`, so this 
is a little difference between the two consumers when consuming the response from the Provider's same endpoint.  


## Contract Test between Provider and Consumer Miku

## Contract Test between Provider and Consumer Nanoha