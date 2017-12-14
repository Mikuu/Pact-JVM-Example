package provider;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import provider.ulti.Nationality;

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
