package ariman.pact.consumer;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProviderService {

    private String backendURL = "http://localhost:8080/information?name=Miku";

    public String getBackendURL() {
        return this.backendURL;
    }

    public void setBackendURL(String URLBase) {
        this.backendURL = URLBase+"/information?name=Miku";
    }
    public void setBackendURL(String URLBase, String name) {
        this.backendURL = URLBase+"/information?name="+name;
    }

    public Information getInformation() {
        RestTemplate restTemplate = new RestTemplate();
        Information information = restTemplate.getForObject(getBackendURL(), Information.class);

        return information;
    }
}
