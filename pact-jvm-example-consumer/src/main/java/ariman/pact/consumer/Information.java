package ariman.pact.consumer;

import java.util.HashMap;
import java.util.Map;

public class Information {
    private Integer salary;
    private String name;
    private Map<String, String> contact = new HashMap<String, String>();

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getContact() {
        return contact;
    }

    public void setContact(Map<String, String> contact) {
        this.contact = contact;
    }


}
