package provider;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("pact")
@RestController
public class PactController {

    @RequestMapping(value = "/pactStateChange", method = RequestMethod.POST)
    public void providerState(@RequestBody PactState body) {
        switch (body.getState()) {
            case "Base State":
                System.out.println("FBI > Base State ...");
                break;
            case "Junit DSL State":
                System.out.println("FBI > Junit DSL State ...");
                break;
        }
    }
}
