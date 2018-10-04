package backend;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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
import java.util.ArrayList;
import java.util.List;


@Controller    // This means that this class is a Controller
@RequestMapping(path="/backend") // This means URL's start with /backend (after Application path)
public class MainController {
    //Test so that we can see if the server is running.
    @GetMapping(path="/test")
    public @ResponseBody boolean sendTest () {
        return true;
    }

    /** This is the equality test
     * @param urlBase
     * @param urlArgs
     * @param argValues
     * @return
     * @throws URISyntaxException
     */
    @GetMapping(path="/equality")
    public @ResponseBody String checkEquality(@RequestParam String urlBase, @RequestParam String[] urlArgs, @RequestParam String[] argValues) throws URISyntaxException {
        if (urlArgs.length != argValues.length) return "Argument and value amounts do no match"; //Test to see if we have the same amount of values as arguments.
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
        String[] secondaryTests = new String[urlArgs.length - 1]; //An array of strings to store our args and values.
        for (int i = 1; i < urlArgs.length; i++) {
            for(int x = 1; x < urlArgs.length; x++) {
                String temp1 = urlArgs[0]; //For each variation of the arguments move the array up by an index of 1
                String temp2 = argValues[0];
                argValues[0] = argValues[x];
                urlArgs[0] = urlArgs[x];
                argValues[x] = temp2;
                urlArgs[x] = temp1;
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
            return "Equalitytest holds true";
        }
        else return "Equality test does not hold true";
    }

    /** This is the equivalence test
     * @param urlOne
     * @param urlTwo
     * @param checkPath
     * @return
     */
    @GetMapping(path="/equivalence")
    public @ResponseBody String checkEquivalence(@RequestParam String urlOne, @RequestParam String urlTwo,@RequestParam String[] checkPath) {
        RestTemplate restTemplate = new RestTemplate();
        Object primaryTest, secondaryTest;
        try {
            primaryTest = restTemplate.getForObject(urlOne, Object.class);//Get api call return as a json object for both calls.
            secondaryTest = restTemplate.getForObject(urlTwo, Object.class);
        } catch (IllegalArgumentException e) {
            return "IllegalArgumentException";
        } catch (HttpClientErrorException e) {
            return "HttpClientErrorException";
        } catch (ClassCastException e) {
            return "response is not in json format";
        } //Test for the type of Object our response is.
        if (primaryTest.getClass() == JSONObject.class && secondaryTest.getClass() == JSONObject.class) {
            JSONObject object = (JSONObject) primaryTest; //Convert the objects to JSONObjects.
            JSONObject object2 = (JSONObject) secondaryTest;
            for (int i = 0; i < checkPath.length - 1; i++) { //Find the checkpath
                object = object.getJSONObject(checkPath[i]);
                object2 = object2.getJSONObject(checkPath[i]);
            }
            if(testEquality(object.getJSONArray(checkPath[checkPath.length-1]), object2.getJSONArray(checkPath[checkPath.length-1]))) {
                return "Equivalence does hold true";
            }
            else return "Equivalence does not hold true";
    }
        else if (primaryTest.getClass() == JSONArray.class && secondaryTest.getClass() == JSONArray.class) {
            JSONArray array = (JSONArray) primaryTest; //Test for JSONArray
            JSONArray array2 = (JSONArray) secondaryTest;
            if(testEquality(array, array2)) {
                return "Equivalence does hold true";
            }
            else return "Equivalence does not hold true";
        }
        else if (primaryTest.getClass() == ArrayList.class && secondaryTest.getClass() == ArrayList.class) {
            List list = java.util.Arrays.asList(primaryTest); //Test for ArrayList
            List list2 = java.util.Arrays.asList(secondaryTest);
            if(testEquality(list, list2)) {
                return "Equivalence does hold true";
            }
            else return "Equivalence does not hold true";
        }
        else if (primaryTest.getClass() != secondaryTest.getClass()) return "responses not in same format";
        else return "response format not recognised";
    }

    public String buildURL(String urlBase, String[] args, String[] values) { //Simple function for building a url
        String urlBackend = "";
        for(int i = 0; i < args.length; i++) {
            urlBackend += "&" + args[i] + "=" + values[i];
        }
        urlBase += urlBackend;
        return urlBase;
    }

    /** This is the subset test
     * @param urlBase
     * @param values
     * @param checkPath
     * @return
     */
    @GetMapping(path="/subset")
    public @ResponseBody String checkSubset(@RequestParam String urlBase, @RequestParam String[] values, @RequestParam String[] checkPath) {
        RestTemplate restTemplate = new RestTemplate();
        Object primaryTest;
        ArrayList<Object> secondaryTests = new ArrayList<>();
        try {
            primaryTest = restTemplate.getForObject(urlBase, Object.class);//Get api call return as an object
            for (int i = 1; i < values.length; i++) {
                urlBase.replace(values[i-1], values[i]);
                secondaryTests.add(restTemplate.getForObject(urlBase, Object.class));
            }
            if(primaryTest.getClass() == JSONObject.class) {
                for(int i = 0; i < secondaryTests.size(); i++) { //For each secondary result
                    JSONObject object = (JSONObject) primaryTest; //Convert Object to JSONObject
                    JSONObject object2 = (JSONObject) secondaryTests.get(i);
                    for (int x = 0; x < checkPath.length - 1; x++) { //Find the checkPath
                        object = object.getJSONObject(checkPath[x]);
                        object2 = object2.getJSONObject(checkPath[x]);
                    }
                    if(!testEquality(object.getJSONArray(checkPath[checkPath.length-1]), object2.getJSONArray(checkPath[checkPath.length-1]))) {
                        return "Subset test does not hold true";
                    }
                }
                return "subset test does hold true";
            }
            if(primaryTest.getClass() == JSONArray.class) { //Test for JSONArray
                for(int i = 0; i < secondaryTests.size(); i++) {
                    JSONArray array = (JSONArray) primaryTest;
                    JSONArray array2 = (JSONArray) secondaryTests.get(i);
                    if(!testEquality(array, array2)) {
                        return "Subset test does not hold true";
                    }
                }
                return "subset test does hold true";
            }
            else if(primaryTest.getClass() == ArrayList.class) { //Test for Subset
                for(int i = 0; i < secondaryTests.size(); i++) {
                    List list = java.util.Arrays.asList(primaryTest);
                    List list2 = java.util.Arrays.asList(secondaryTests.get(i));;
                    if(!testEquality(list, list2)) {
                        return "Subset test does not hold true";
                    }
                }
                return "subset test does hold true";
            }
        } catch (IllegalArgumentException e) {
            return "IllegalArgumentException";
        } catch (HttpClientErrorException e) {
            return "HttpClientErrorException";
        } catch (ClassCastException e) {
            return "response is not in json format";
        }
        return "";
    }

    public boolean testEquality(JSONArray testArray, JSONArray matchArray) {
        if (testArray.length() != matchArray.length()) return false;
        ArrayList<String> match = new ArrayList<>();
        ArrayList<String> test = new ArrayList<>();
        for (int i = 0; i < testArray.length(); i++) {
            match.add(matchArray.get(i).toString());
            test.add(testArray.get(i).toString());
        }
        while(test.size() > 0) {
            boolean matched = false;
            for (int i = 0; i < match.size(); i++) {
                if (test.get(0).equals(match.get(i)) && !matched) {
                    test.remove(0);
                    match.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }
        return true;
    }

    public boolean testEquality(List testArray, List matchArray) {
        if (testArray.size() != matchArray.size()) return false;
        ArrayList<String> match = new ArrayList<>();
        ArrayList<String> test = new ArrayList<>();
        for (int i = 0; i < testArray.size(); i++) {
            match.add(matchArray.get(i).toString());
            test.add(testArray.get(i).toString());
        }
        while(test.size() > 0) {
            boolean matched = false;
            for (int i = 0; i < match.size(); i++) {
                if (test.get(0).equals(match.get(i)) && !matched) {
                    test.remove(0);
                    match.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }
        return true;
    }

    /** The disjoint test expects the two result sets to be completely different from one another.
     *
     * @param urlOne
     * @param urlTwo
     * @param checkPath
     * @return
     */
    @GetMapping(path="/disjoint")
    public @ResponseBody String checkDisjoint(@RequestParam String urlOne, @RequestParam String urlTwo, @RequestParam String[] checkPath) {
        RestTemplate restTemplate = new RestTemplate(); //Creates the rest template for API calls to RESTful apis.

        Object firstResponse = restTemplate.getForObject(urlOne, Object.class); //We get the response of the first call and second as an Object.
        Object secondResponse = restTemplate.getForObject(urlTwo, Object.class);

        //We check the type of Object the response was as we must act differently for different responses
        // If the response is a JSON object we must first find the JSONArray contaning the data to be tested if there is a check path.
        if(firstResponse.getClass() == JSONObject.class) {
            JSONObject object = (JSONObject) firstResponse; //Cast the first and second responses from Object to JSONObject type.
            JSONObject object2 = (JSONObject) secondResponse;
            for (int x = 0; x < checkPath.length - 1; x++) { //If there is a check path then we navigate through the object to the destination we want to test.
                object = object.getJSONObject(checkPath[x]);
                object2 = object2.getJSONObject(checkPath[x]);
            }
            if(testEquality(object.getJSONArray(checkPath[checkPath.length-1]), object2.getJSONArray(checkPath[checkPath.length-1]))) {
                return "Disjoint does not hold true"; //If the test for equality returns true then the two objects are not in disjoint.
            }
            else return "Disjoint does hold true"; //If true is not returned then the two objects must be in disjoint.
        }
        //If the objects type is of JSONArray then we can test each part of the array without using a check path.
        else if(firstResponse.getClass() == JSONArray.class) {
            JSONArray object = (JSONArray) firstResponse; //Cast the first and second response from Object to JSONArray
            JSONArray object2 = (JSONArray) secondResponse;
            if(!testEquality(object, object2)) { //If the test for equality returns true then the two objects are not in disjoint.
                return "Disjoint does not hold true"; //If true is not returned then the two objects must be in disjoint.
            }
            else return "Disjoint does hold true";
        }
        //If the objects type is of ArrayList then we can do a similiar test to JSONArray with slightly different syntax.
        else if(firstResponse.getClass() == ArrayList.class) {
            List list = java.util.Arrays.asList(firstResponse); //Convert first and second responses from Object to ArrayList.
            List list2 = java.util.Arrays.asList(secondResponse);;
            if(!testEquality(list, list2)) {
                return "Disjoint does not hold true"; //If the test for equality returns true then the two objects are not in disjoint.
            }
            else return "Disjoint does hold true"; //If true is not returned then the two objects must be in disjoint.
        }
        else return "Unknown Format type " + firstResponse.getClass(); //This will return the objects type and an error message if the response format is not known.
    }

    @GetMapping(path="/complete")
    public @ResponseBody String checkComplete(@RequestParam String urlBase, @RequestParam String[] values, @RequestParam String[] checkPath) {
        RestTemplate restTemplate = new RestTemplate();

        Object firstResponse = restTemplate.getForObject(urlBase, Object.class);
        ArrayList<Object> secondaryResponses = new ArrayList<>();
        for (int i = 1; i < values.length; i++) {
            urlBase.replace(values[i-1], values[i]);
            secondaryResponses.add(restTemplate.getForObject(urlBase, Object.class));
        }

        return "";
    }

    public boolean testComplete(ArrayList<JSONArray> secondaryResponses, JSONArray firstResponse) {

        return true;
    }

    public boolean testComplete(ArrayList<List> secondaryResponses, List firstResponse) {

        return true;
    }
}
