package ariman.pact.consumer;

import org.springframework.web.client.RestTemplate;

public class ProviderHandler {
    String backendURLBase = "http://localhost:8080";

    public String getBackendURLBase() {
        return backendURLBase;
    }

    public void setBackendURLBase(String backendURLBase) {
        this.backendURLBase = backendURLBase;
    }

    public String getBackendURL() {
        return this.getBackendURLBase()+"/information?name=Miku";
    }

    public Information getInformation() {
        RestTemplate restTemplate = new RestTemplate();
        Information information = restTemplate.getForObject(getBackendURL(), Information.class);

        return information;
    }
}
