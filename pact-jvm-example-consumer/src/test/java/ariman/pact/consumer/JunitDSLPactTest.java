package ariman.pact.consumer;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.junit.Assert.assertEquals;

public class JunitDSLPactTest {
    PactSpecVersion pactSpecVersion;

    @Test
    public void testPact() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;charset=UTF-8");

        RequestResponsePact pact = ConsumerPactBuilder
            .consumer("PactJVMExampleConsumerJunitDSL")
            .hasPactWith("PactJVMExampleProvider")
            .given("Test state")
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
                        "    \"contact\": {\n" +
                        "        \"Email\": \"hatsune.miku@ariman.com\",\n" +
                        "        \"Phone Number\": \"9090950\"\n" +
                        "    }\n" +
                        "}")
            .uponReceiving("Query name is Nanoha")
                .path("/information")
                .query("name=Nanoha")
                .method("GET")
            .willRespondWith()
                .headers(headers)
                .status(200)
                .body("{\n" +
                        "    \"salary\": 0,\n" +
                        "    \"name\": \"Nanoha\",\n" +
                        "    \"contact\": null\n" +
                        "}")
            .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault(this.pactSpecVersion.V2);
        PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
            ProviderHandler providerHandler = new ProviderHandler();
            providerHandler.setBackendURL(mockServer.getUrl(), "Nanoha");
            Information information = providerHandler.getInformation();
            assertEquals(information.getName(), "Nanoha");

            providerHandler.setBackendURL(mockServer.getUrl());
            information = providerHandler.getInformation();
            assertEquals(information.getName(), "Hatsune Miku");
        });

        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error)result).getError());
        }

        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }

}
