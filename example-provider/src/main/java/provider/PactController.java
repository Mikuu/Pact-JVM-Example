package provider;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import provider.ulti.Nationality;

@Profile("pact")
@RestController
public class PactController {

    @RequestMapping(value = "/pactStateChange", method = RequestMethod.POST)
    public PactStateChangeResponseDTO providerState(@RequestBody PactState body) {
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

        // This response is not mandatory for Pact state change. The only reason is the current Pact-JVM v4.0.3 does
        // check the stateChange request's response, more exactly, checking the response's Content-Type, couldn't be
        // null, so it MUST return something here.
        PactStateChangeResponseDTO pactStateChangeResponse = new PactStateChangeResponseDTO();
        pactStateChangeResponse.setState(body.getState());

        return pactStateChangeResponse;
    }
}
