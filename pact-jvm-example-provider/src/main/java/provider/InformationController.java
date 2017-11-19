package provider;

import java.util.HashMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InformationController {

    private Information information = new Information();

    @RequestMapping("/information")
    public Information information(@RequestParam(value="name", defaultValue="Miku") String name) {
        if (name.equals("Miku")) {
            HashMap contact = new HashMap<String, String>();
            contact.put("Email", "hatsune.miku@ariman.com");
            contact.put("Phone Number", "9090950");
            information.setContact(contact);
            information.setName("Hatsune Miku");
            information.setSalary(45000);
        } else {
            information.setContact(null);
            information.setName(name);
            information.setSalary(0);
        }

        return information;
    }
}
