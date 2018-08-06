package ariman.pact.consumer;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.junit.Assert.assertEquals;
import static io.pactfoundation.consumer.dsl.LambdaDsl.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NationalityPactTest {
    PactSpecVersion pactSpecVersion;

    @Autowired
    ProviderService providerService;
    
    private void checkResult(PactVerificationResult result) {
        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error)result).getError());
        }
        assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }

    @Test
    public void testWithNationality() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;charset=UTF-8");

        DslPart body = newJsonBody((root) -> {
            root.numberType("salary");
            root.stringValue("name", "Takamachi Nanoha");
            root.stringValue("nationality", "Japan");
            root.object("contact", (contactObject) -> {
                contactObject.stringMatcher("Email", ".*@ariman.com", "takamachi.nanoha@ariman.com");
                contactObject.stringType("Phone Number", "9090940");
            });
        }).build();

        RequestResponsePact pact = ConsumerPactBuilder
            .consumer("ConsumerNanohaWithNationality")
            .hasPactWith("ExampleProvider")
            .given("")
            .uponReceiving("Query name is Nanoha")
                .path("/information")
                .query("name=Nanoha")
                .method("GET")
            .willRespondWith()
                .headers(headers)
                .status(200)
                .body(body)
            .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault(this.pactSpecVersion.V3);
        PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
            providerService.setBackendURL(mockServer.getUrl());
            Information information = providerService.getInformation();
            assertEquals(information.getName(), "Takamachi Nanoha");
            assertEquals(information.getNationality(), "Japan");
        });

        checkResult(result);
    }

    @Test
    public void testNoNationality() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json;charset=UTF-8");

        DslPart body = newJsonBody((root) -> {
            root.numberType("salary");
            root.stringValue("name", "Takamachi Nanoha");
            root.stringValue("nationality", null);
            root.object("contact", (contactObject) -> {
                contactObject.stringMatcher("Email", ".*@ariman.com", "takamachi.nanoha@ariman.com");
                contactObject.stringType("Phone Number", "9090940");
            });
        }).build();

        RequestResponsePact pact = ConsumerPactBuilder
            .consumer("ConsumerNanohaNoNationality")
            .hasPactWith("ExampleProvider")
            .given("No nationality")
            .uponReceiving("Query name is Nanoha")
                .path("/information")
                .query("name=Nanoha")
                .method("GET")
            .willRespondWith()
                .headers(headers)
                .status(200)
                .body(body)
            .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault(this.pactSpecVersion.V3);
        PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
            providerService.setBackendURL(mockServer.getUrl());
            Information information = providerService.getInformation();
            assertEquals(information.getName(), "Takamachi Nanoha");
            assertEquals(information.getNationality(), null);
        });

        checkResult(result);
    }

}
