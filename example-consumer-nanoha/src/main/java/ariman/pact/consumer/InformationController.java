package ariman.pact.consumer;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class InformationController {

    @RequestMapping("/nanoha")
    public String miku(Model model) {
        Information information = new ProviderHandler().getInformation();
        model.addAttribute("name", information.getName());
        model.addAttribute("nationality", information.getNationality());
        model.addAttribute("mail", information.getContact().get("Email"));
        model.addAttribute("phone", information.getContact().get("Phone Number"));

        return "nanoha";
    }

}
