package backend;

import org.json.JSONString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.web.client.RestTemplate;




@Controller    // This means that this class is a Controller
@RequestMapping(path="/backend") // This means URL's start with /backend (after Application path)
public class MainController {
    @GetMapping(path="/test")
    public @ResponseBody String sendTest () {
        return "System Running";
    }

    @GetMapping(path="/equivalence")
    public @ResponseBody String checkEquivalence(@RequestParam String urlBase, @RequestParam String[] urlArgs, @RequestParam String[] argValues) {
        if (urlArgs.length != argValues.length) return "Argument and value amounts do no match";
        String url = buildURL(urlBase, urlArgs, argValues);
        RestTemplate restTemplate = new RestTemplate();
        JSONObject primaryTest = restTemplate.getForObject(url, JSONObject.class);
        JSONObject[] secondaryTests = new JSONObject[urlArgs.length - 1];
        for (int i = 1; i < urlArgs.length; i++) {
            for(int x = 1; x < urlArgs.length; x++) {
                String temp1 = urlArgs[0];
                String temp2 = argValues[0];
                argValues[0] = argValues[x];
                urlArgs[0] = urlArgs[x];
                argValues[x] = temp2;
                urlArgs[x] = temp1;
                url = buildURL(urlBase, urlArgs, argValues);
                secondaryTests[x-1] = restTemplate.getForObject(url, JSONObject.class);
            }
        }
        boolean testsHeldTrue = true;
        for (int i = 0; i < secondaryTests.length; i++) {
            if(!primaryTest.toString().equals(secondaryTests[i].toString())) testsHeldTrue = false;
        }
        if(testsHeldTrue){
            return "Equivalence test holds true";
        }
        else return "Equivalence test does not hold true";
    }

    public String buildURL(String urlBase, String[] args, String[] values) {
        String urlBackend = "";
        for(int i = 0; i < args.length; i++) {
            urlBackend += "&" + args[i] + "=" + values[i];
        }
        urlBase += urlBackend;
        return urlBase;
    }

}
