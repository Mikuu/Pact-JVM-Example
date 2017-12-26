# PACT JVM Example

These codes provide an example about how to do Contract Test with PACT JVM Junit, 
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

Now, it's time to look into the tests. 

> This README will not go through all tests line by line, because 
the tests themselves are very simple and straight forward, so I will only point out some highlights for 
each test. For detailed explanation about the codes, please refer the official [document](https://github.com/DiUS/pact-jvm/tree/be4a32b08ebbd89321dc37cbfe838fdac37774b3/pact-jvm-consumer-junit)

By the time this example is created, PACT JVM Junit provides 3 ways to write the pact test file 
at consumer side, the **Basic Junit**, the **Junit Rule** and **Junit DSL**.


#### Basic Junit
`PactBaseConsumerTest.java`
```java
public class PactBaseConsumerTest extends ConsumerPactTestMk2 {

    @Override
    @Pact(provider="ExampleProvider", consumer="BaseConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;charset=UTF-8");

        return builder
                .given("")
                .uponReceiving("Pact JVM example Pact interaction")
                .path("/information")
                .query("name=Miku")
                .method("GET")
                .willRespondWith()
                .headers(headers)
                .status(200)
                .body("{\n" +
                        "    \"salary\": 45000,\n" +
                        "    \"name\": \"Hatsune Miku\",\n" +
                        "    \"nationality\": \"Japan\",\n" +
                        "    \"contact\": {\n" +
                        "        \"Email\": \"hatsune.miku@ariman.com\",\n" +
                        "        \"Phone Number\": \"9090950\"\n" +
                        "    }\n" +
                        "}")

                .toPact();
    }

    @Override
    protected String providerName() {
        return "ExampleProvider";
    }

    @Override
    protected String consumerName() {
        return "BaseConsumer";
    }

    @Override
    protected void runTest(MockServer mockServer) throws IOException {
        ProviderHandler providerHandler = new ProviderHandler();
        providerHandler.setBackendURL(mockServer.getUrl());
        Information information = providerHandler.getInformation();
        assertEquals(information.getName(), "Hatsune Miku");
    }
}
```
The `ProviderHandler` is the same one used in consumer Miku, we just use it to do a self 
integration test, the purpose for this is to check if consumer Miku can handle the mocked 
response correctly, then ensure the Pact content created are just as we need before we send 
it to Provider.

`mockServer.getUrl()` can return the mock server's url, which is to be used in our handler.

#### Junit Rule
`PactJunitRuleTest.java`
```java
public class PactJunitRuleTest {

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("ExampleProvider",this);

    @Pact(consumer="JunitRuleConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;charset=UTF-8");

        return builder
                .given("")
                .uponReceiving("Pact JVM example Pact interaction")
                .path("/information")
                .query("name=Miku")
                .method("GET")
                .willRespondWith()
                .headers(headers)
                .status(200)
                .body("{\n" +
                        "    \"salary\": 45000,\n" +
                        "    \"name\": \"Hatsune Miku\",\n" +
                        "    \"nationality\": \"Japan\",\n" +
                        "    \"contact\": {\n" +
                        "        \"Email\": \"hatsune.miku@ariman.com\",\n" +
                        "        \"Phone Number\": \"9090950\"\n" +
                        "    }\n" +
                        "}")
                .toPact();
    }

    @Test
    @PactVerification
    public void runTest() {
        ProviderHandler providerHandler = new ProviderHandler();
        providerHandler.setBackendURL(mockProvider.getUrl());
        Information information = providerHandler.getInformation();
        assertEquals(information.getName(), "Hatsune Miku");
    }
}
```
This test uses Junit Rule which can simplify the writing of test cases comparing with the Basic Junit.

`PactJunitRuleMultipleInteractionsTest.java`
```java
public class PactJunitRuleMultipleInteractionsTest {

    @Rule
    public PactProviderRuleMk2 mockProvider = new PactProviderRuleMk2("ExampleProvider",this);

    @Pact(consumer="JunitRuleMultipleInteractionsConsumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;charset=UTF-8");

        return builder
                .given("")
                .uponReceiving("Miku")
                .path("/information")
                .query("name=Miku")
                .method("GET")
                .willRespondWith()
                .headers(headers)
                .status(200)
                .body("{\n" +
                        "    \"salary\": 45000,\n" +
                        "    \"name\": \"Hatsune Miku\",\n" +
                        "    \"nationality\": \"Japan\",\n" +
                        "    \"contact\": {\n" +
                        "        \"Email\": \"hatsune.miku@ariman.com\",\n" +
                        "        \"Phone Number\": \"9090950\"\n" +
                        "    }\n" +
                        "}")
                .given("")
                .uponReceiving("Nanoha")
                .path("/information")
                .query("name=Nanoha")
                .method("GET")
                .willRespondWith()
                .headers(headers)
                .status(200)
                .body("{\n" +
                        "    \"salary\": 80000,\n" +
                        "    \"name\": \"Takamachi Nanoha\",\n" +
                        "    \"nationality\": \"Japan\",\n" +
                        "    \"contact\": {\n" +
                        "        \"Email\": \"takamachi.nanoha@ariman.com\",\n" +
                        "        \"Phone Number\": \"9090940\"\n" +
                        "    }\n" +
                        "}")
                .toPact();
    }

    @Test
    @PactVerification()
    public void runTest() {
        ProviderHandler providerHandler = new ProviderHandler();
        providerHandler.setBackendURL(mockProvider.getUrl());
        Information information = providerHandler.getInformation();
        assertEquals(information.getName(), "Hatsune Miku");

        providerHandler.setBackendURL(mockProvider.getUrl(), "Nanoha");
        information = providerHandler.getInformation();
        assertEquals(information.getName(), "Takamachi Nanoha");
    }
}
```
This case uses Junit Rule too, but with two interactions in one Pact file.

## Contract Test between Provider and Consumer Nanoha