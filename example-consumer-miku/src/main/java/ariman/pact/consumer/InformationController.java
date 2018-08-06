package ariman.pact.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class InformationController {

    @Autowired
    private ProviderService providerService;

    @RequestMapping("/miku")
    public String miku(Model model) {
        Information information = providerService.getInformation();
        model.addAttribute("name", information.getName());
        model.addAttribute("mail", information.getContact().get("Email"));
        model.addAttribute("phone", information.getContact().get("Phone Number"));

        return "miku";
    }

}
