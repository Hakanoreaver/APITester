package backend;

import org.json.JSONString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.web.client.RestTemplate;




@Controller    // This means that this class is a Controller
@RequestMapping(path="/backend") // This means URL's start with /demo (after Application path)
public class MainController {
    @GetMapping(path="/test")
    public @ResponseBody String sendTest () {
        return "System Running";
    }

    @GetMapping(path="/equivalence")
    public @ResponseBody String checkEquivalence(@RequestParam String urlBase, @RequestParam String[] urlArgs, @RequestParam String[] argValues) {
        if (urlArgs.length != argValues.length) return "Argument and value amounts do no match";
        String urlBackend = "";
        for(int i = 0; i < urlArgs.length; i++) {
            urlBackend += "&" + urlArgs[i] + "=" + argValues[i];
        }
        urlBase += urlBackend;
        RestTemplate restTemplate = new RestTemplate();
        JSONObject primaryTest = restTemplate.getForObject(urlBackend, JSONObject.class);
        String[] secondaryTests = new String[urlArgs.length - 1];
        JSONObject secondaryTest= null;
        if(primaryTest.toString().equals(secondaryTest.toString())){
            return "Equivalence test holds true";
        }
        else return "Equivalence test does not hold true";
    }
}
