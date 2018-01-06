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

### Create Test Cases

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

#### Junit DSL
`PactJunitDSLTest`
```java
public class PactJunitDSLTest {

    private void checkResult(PactVerificationResult result) {
        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error)result).getError());
        }
        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }

    @Test
    public void testPact1() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;charset=UTF-8");

        RequestResponsePact pact = ConsumerPactBuilder
            .consumer("JunitDSLConsumer1")
            .hasPactWith("ExampleProvider")
            .given("")
            .uponReceiving("Query name is Miku")
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

        MockProviderConfig config = MockProviderConfig.createDefault();
        PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
            ProviderHandler providerHandler = new ProviderHandler();
            providerHandler.setBackendURL(mockServer.getUrl(), "Miku");
            Information information = providerHandler.getInformation();
            assertEquals(information.getName(), "Hatsune Miku");
        });

        checkResult(result);
    }

    @Test
    public void testPact2() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;charset=UTF-8");

        RequestResponsePact pact = ConsumerPactBuilder
            .consumer("JunitDSLConsumer2")
            .hasPactWith("ExampleProvider")
            .given("")
            .uponReceiving("Query name is Nanoha")
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

        MockProviderConfig config = MockProviderConfig.createDefault();
        PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
            ProviderHandler providerHandler = new ProviderHandler();
            providerHandler.setBackendURL(mockServer.getUrl(), "Nanoha");

            Information information = providerHandler.getInformation();
            assertEquals(information.getName(), "Takamachi Nanoha");
        });

        checkResult(result);
    }
}
```
Comparing with Basic Junit and Junit Rule usage, the DSL provides the ability to create multiple Pact files 
in one test class.

`PactJunitDSLJsonBodyTest`
```java
public class PactJunitDSLJsonBodyTest {
    PactSpecVersion pactSpecVersion;

    private void checkResult(PactVerificationResult result) {
        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error)result).getError());
        }
        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }

    @Test
    public void testWithPactDSLJsonBody() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;charset=UTF-8");

        DslPart body = new PactDslJsonBody()
                .numberType("salary", 45000)
                .stringType("name", "Hatsune Miku")
                .stringType("nationality", "Japan")
                .object("contact")
                .stringValue("Email", "hatsune.miku@ariman.com")
                .stringValue("Phone Number", "9090950")
                .closeObject();

        RequestResponsePact pact = ConsumerPactBuilder
            .consumer("JunitDSLJsonBodyConsumer")
            .hasPactWith("ExampleProvider")
            .given("")
            .uponReceiving("Query name is Miku")
                .path("/information")
                .query("name=Miku")
                .method("GET")
            .willRespondWith()
                .headers(headers)
                .status(200)
                .body(body)
            .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault(this.pactSpecVersion.V3);
        PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
            ProviderHandler providerHandler = new ProviderHandler();
            providerHandler.setBackendURL(mockServer.getUrl());
            Information information = providerHandler.getInformation();
            assertEquals(information.getName(), "Hatsune Miku");
        });

        checkResult(result);
    }
    @Test
    public void testWithLambdaDSLJsonBody() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;charset=UTF-8");

        DslPart body = newJsonBody((root) -> {
            root.numberValue("salary", 45000);
            root.stringValue("name", "Hatsune Miku");
            root.stringValue("nationality", "Japan");
            root.object("contact", (contactObject) -> {
                contactObject.stringMatcher("Email", ".*@ariman.com", "hatsune.miku@ariman.com");
                contactObject.stringType("Phone Number", "9090950");
            });
        }).build();

        RequestResponsePact pact = ConsumerPactBuilder
            .consumer("JunitDSLLambdaJsonBodyConsumer")
            .hasPactWith("ExampleProvider")
            .given("")
            .uponReceiving("Query name is Miku")
                .path("/information")
                .query("name=Miku")
                .method("GET")
            .willRespondWith()
                .headers(headers)
                .status(200)
                .body(body)
            .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault(this.pactSpecVersion.V3);
        PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
            ProviderHandler providerHandler = new ProviderHandler();
            providerHandler.setBackendURL(mockServer.getUrl());
            Information information = providerHandler.getInformation();
            assertEquals(information.getName(), "Hatsune Miku");
        });

        checkResult(result);
    }

}
```
When use Json Body in DSL usage, we can control the test accuracy by defining whether we check the response 
body's attributes by Value or by Type, or even with Regular Expression. Comparing with normal Json body, the 
Lambda statements can avoid using `.close**()` methods to make the codes more clean. You can find more 
description about it [here](https://github.com/DiUS/pact-jvm/tree/be4a32b08ebbd89321dc37cbfe838fdac37774b3/pact-jvm-consumer-java8).  

### Run the Tests at Consumer Miku side
Because we are using Junit, so to run the tests and create Pact files are very easy, just as what we always 
run our usual Unit Test:

`./gradlew :example-consumer-miku:test`

After that, you can find 7 JSON files created in folder `Pacts\Miku`. These are the Pacts which contain the 
contract between Miku and Provider, and these Pacts will be used to drive the Contract Test later at Provider 
side.

### Run the Contract Test at Provider side
We are using the Pact Gradle task in Provider side to run the Contract Test, which can be very easy without 
writing any code, just execute the `pactVerify` task. **Before we run the test, make sure the Provider API is already started and running at our localhost.**

Then we can run the test as:

`./gradlew :example-provider:pactVerify`

and you can find the test results at the command line, would be something likes:

```commandline
Arimans-MacBook-Pro:pact-jvm-example ariman$ ./gradlew :example-provider:pactVerify                                          

> Task :example-provider:pactVerify_ExampleProvider 

Verifying a pact between Miku - Base contract and ExampleProvider
  [Using File /Users/ariman/Workspace/Pacting/pact-jvm-example/Pacts/Miku/BaseConsumer-ExampleProvider.json]
  Given 
         WARNING: State Change ignored as there is no stateChange URL
  Consumer Miku
    returns a response which
      has status code 200 (OK)
      includes headers
        "Content-Type" with value "application/json;charset=UTF-8" (OK)
      has a matching body (OK)
  Given 
         WARNING: State Change ignored as there is no stateChange URL
  Pact JVM example Pact interaction
    returns a response which
      has status code 200 (OK)
      includes headers
        "Content-Type" with value "application/json;charset=UTF-8" (OK)
      has a matching body (OK)
```

### Gradle Configuration

Before we continue to Consumer Nanoha, let's look at the Gradle Configuration first:

```groovy
project(':example-consumer-miku') {
    jar {
        baseName = 'example-consumer-miku'
        version =  '1.0.0'
    }

    test {
        systemProperties['pact.rootDir'] = "$rootDir/Pacts/Miku"
    }
}


project(':example-consumer-nanoha') {
    jar {
        baseName = 'example-consumer-nanoha'
        version =  '1.0.0'
    }

    test {
        systemProperties['pact.rootDir'] = "$rootDir/Pacts/Nanoha"
    }
}

import java.net.URL

project(':example-provider') {
    apply plugin: 'au.com.dius.pact'

    jar {
        baseName = 'example-provider'
        version =  '1.0.0'
    }

    pact {
        serviceProviders {
            ExampleProvider {
                protocol = 'http'
                host = 'localhost'
                port = 8080
                path = '/'

                hasPactWith('Miku - Base contract') {
                    pactSource = file("$rootDir/Pacts/Miku/BaseConsumer-ExampleProvider.json")
                }

                hasPactsWith('Miku - All contracts') {
                    pactFileLocation = file("$rootDir/Pacts/Miku")
                }

//                hasPactWith('Nanoha - With Nantionality') {
//                    pactSource = file("$rootDir/Pacts/Nanoha/ConsumerNanohaWithNationality-ExampleProvider.json")
//                }

//                hasPactWith('Nanoha - No Nantionality') {
//                    stateChangeUrl = new URL('http://localhost:8080/pactStateChange')
//                    pactSource = file("$rootDir/Pacts/Nanoha/ConsumerNanohaNoNationality-ExampleProvider.json")
//                }
            }
        }
    }
```

Here we have configuration separately for Consumer Nanoha, Consumer Miku and Provider.

`systemProperties['pact.rootDir'] = "$rootDir/Pacts/Nanoha"` define the path where both consumer Miku and Nanoha 
will generate their Pacts files. In this example, we just put it at a local path and Provider can fetch these pacts 
 directly, but for real project, you can firstly generate the pacts locally and then transmit them to a place where 
 the Provider can get, or upload them to Pact Broker.
 
 Then in Provider configuration, use `hasPactWith()` or `hasPactsWith()` to specify the path to find those Pacts.
 
 *Why comment out Nanoha's Pacts path?* Because we haven't created Nanoha's Pacts, so it will raise exception with 
 that configuration, we can enable that path later after we created the Nanoha's Pacts files.

## Contract Test between Provider and Consumer Nanoha

The Contract Test between Nanoha and Provider is similar to Miku, the difference I want to demonstrate here is 
**Provider State**, you can find more detailed explanation about the Provider State [here](https://docs.pact.io/documentation/provider_states.html).

### Preparation for Provider State

In our example Provider, the returned payload for both Miku and Nanoha have a property `.nationality`, this kind 
of properties values, in real project, could always be queried from database or dependency service, but in this 
example, to simplify the Provider application logic, I just create a static property to simulate the data storage:

`provider.ulti.Nationality`

```java
public class Nationality {
    private static String nationality = "Japan";

    public static String getNationality() {
        return nationality;
    }

    public static void setNationality(String nationality) {
        Nationality.nationality = nationality;
    }
}
```

Then we can change the `.nationality` value at anytime to achieve our test purpose. To change its value, I created 
a `PactController` which can accept and reset the state value by sending a POST to endpoint `pactStateChange`:

`provider.PactController`

```java
@Profile("pact")
@RestController
public class PactController {

    @RequestMapping(value = "/pactStateChange", method = RequestMethod.POST)
    public void providerState(@RequestBody PactState body) {
        switch (body.getState()) {
            case "No nationality":
                Nationality.setNationality(null);
                System.out.println("Pact State Change >> remove nationality ...");
                break;
            case "Default nationality":
                Nationality.setNationality("Japan");
                System.out.println("Pact Sate Change >> set default nationality ...");
                break;
        }
    }
}
```

Because this controller should only be available at **Non-Production environment**, so I added a Profile annotation 
to limit that this endpoint is only existing when Provider application is running with a "pact" profile, in another 
words, it only works in test environment.

**So, to summary all above things, when Provider is running with a "pact" profile, it serves an endpoint `pactStateChange` 
which accept a POST request to set the `.nationality` value to be "Japan" (by default) or "null"**. 

### Create Test Case

### Run Contract Test at Provider side