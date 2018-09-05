package backend;

import org.json.JSONString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;


@Controller    // This means that this class is a Controller
@RequestMapping(path="/backend") // This means URL's start with /backend (after Application path)
public class MainController {
    @GetMapping(path="/test")
    public @ResponseBody String sendTest () {
        RestTemplate test = new RestTemplate();
        return test.getForObject("http://localhost:8080/demo/event/byDate?month=4&year=1990", String.class);
    }

    @GetMapping(path="/equivalence")
    public @ResponseBody String checkEquivalence(@RequestParam String urlBase, @RequestParam String[] urlArgs, @RequestParam String[] argValues) throws URISyntaxException {
        if (urlArgs.length != argValues.length) return "Argument and value amounts do no match";
        String url = buildURL(urlBase, urlArgs, argValues); // Build our first url for primary call.
        RestTemplate restTemplate = new RestTemplate(); //Rest template allows us to call RESTful API
        String primaryTest;
        try {
            primaryTest = restTemplate.getForObject(url, String.class);//Get api call return as a json object
        } catch (IllegalArgumentException e) {
            return "IllegalArgumentException at primary for url " + url;
        }
        catch (HttpClientErrorException e) {
            return "HttpClientErrorException at primary for url " + url;
        }
        String[] secondaryTests = new String[urlArgs.length - 1];
        for (int i = 1; i < urlArgs.length; i++) {
            for(int x = 1; x < urlArgs.length; x++) {
                String temp1 = urlArgs[0]; //For each variation of the arguments move the array up by an index of 1
                String temp2 = argValues[0];
                argValues[0] = argValues[x];
                urlArgs[0] = urlArgs[x];
                argValues[x] = temp2;
                urlArgs[x] = temp1;
                System.out.println("hello");
            }
                try {
                    url = buildURL(urlBase, urlArgs, argValues);//Build a new url for the re ordered arguments
                    secondaryTests[i-1] = restTemplate.getForObject(url, String.class);//Store the response as a JSON object
                } catch (IllegalArgumentException e) {
                    return "IllegalArgumentException at secondary for url " + url;
                }
                catch (HttpClientErrorException e) {
                    return "HttpClientErrorException at secondary for url " + url;
                }

        }
        boolean testsHeldTrue = true;
        for (int i = 0; i < secondaryTests.length; i++) { //Check if the returns are identical despite different argument order
            if(!primaryTest.equals(secondaryTests[i])) testsHeldTrue = false;
        }
        if(testsHeldTrue){
            return "Equivalence test holds true";
        }
        else return "Equivalence test does not hold true";
    }

    public String buildURL(String urlBase, String[] args, String[] values) { //Simple function for building a url
        String urlBackend = "";
        for(int i = 0; i < args.length; i++) {
            urlBackend += "&" + args[i] + "=" + values[i];
        }
        urlBase += urlBackend;
        return urlBase;
    }

}
