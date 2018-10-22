package backend;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


@Controller    // This means that this class is a Controller
@RequestMapping(path = "/backend") // This means URL's start with /backend (after Application path)
public class MainController {
    //Test so that we can see if the server is running.
    @GetMapping(path = "/test")
    public @ResponseBody
    boolean sendTest() {

        return true;
    }

    HttpHeaders createHeaders(String username, String password) {
        return new HttpHeaders() {{
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(Charset.forName("US-ASCII")));
            String authHeader = "Basic " + new String(encodedAuth);
            set("Authorization", authHeader);
        }};
    }

    /**
     * This is the equality test
     *
     * @param urlBase
     * @param urlArgs
     * @param argValues
     * @return
     */
    @GetMapping(path = "/equality")
    public @ResponseBody
    String checkEquality(@RequestParam String urlBase, @RequestParam String[] urlArgs, @RequestParam String[] argValues, String name, String token) {
        if (urlArgs.length != argValues.length)
            return "Argument and value amounts do no match"; //Test to see if we have the same amount of values as arguments.
        String url = buildURL(urlBase, urlArgs, argValues); // Build our first url for primary call.
        RestTemplate restTemplate = new RestTemplate(); //Rest template allows us to call RESTful API

        String primaryTest = getReturn(name, token, url).get("body").toString();

        ArrayList<String> secondaryTests = new ArrayList<>(); //An array of strings to store our args and values.
        for (int i = 1; i < urlArgs.length; i++) {
            for (int x = 1; x < urlArgs.length; x++) {
                String temp1 = urlArgs[0]; //For each variation of the arguments move the array up by an index of 1
                String temp2 = argValues[0];
                argValues[0] = argValues[x];
                urlArgs[0] = urlArgs[x];
                argValues[x] = temp2;
                urlArgs[x] = temp1;
            }
            url = buildURL(urlBase, urlArgs, argValues);//Build a new url for the re ordered arguments
            secondaryTests.add(getReturn(name, token, url).get("body").toString());
        }
        boolean testsHeldTrue = true;
        for (int i = 0; i < secondaryTests.size(); i++) { //Check if the returns are identical despite different argument order
            if (!primaryTest.equals(secondaryTests.get(i))) {
                return "Equality test does not hold true \n" + primaryTest + "\n" + secondaryTests.get(i);
            }
        }
        return "Equality test holds true";
    }

    public JSONObject getReturn(String name, String token, String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(name, token);
        HttpEntity entity = new HttpEntity(headers);
        System.out.println("Here");
        HttpEntity temp = restTemplate.exchange(url, HttpMethod.GET, entity, Object.class);
        JSONObject obj = new JSONObject(temp);
        return obj;
    }

    /**
     * This is the equivalence test
     *
     * @param urlOne
     * @param urlTwo
     * @param checkPath
     * @return
     */
    @GetMapping(path = "/equivalence")
    public @ResponseBody
    String checkEquivalence(@RequestParam String urlOne, @RequestParam String urlTwo, @RequestParam String[] checkPath, String name, String token) {


        HttpEntity<Object> primary, secondary;
        JSONObject primaryTest, secondaryTest;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set(name, token);
        HttpEntity entity = new HttpEntity(headers);
        System.out.println(urlOne);
        primary = restTemplate.exchange(urlOne, HttpMethod.GET, entity, Object.class);
        secondary = restTemplate.exchange(urlTwo, HttpMethod.GET, entity, Object.class);

        primaryTest = new JSONObject(primary);
        secondaryTest = new JSONObject(secondary);


        try {
            // primaryTest = getReturn(name, token, urlOne);
            //secondaryTest = getReturn(name, token, urlTwo);

        } catch (IllegalArgumentException e) {
            return "IllegalArgumentException";
        } catch (HttpClientErrorException e) {
            return "HttpClientErrorException" + e.toString();
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
            if (testEquality(object.getJSONArray(checkPath[checkPath.length - 1]), object2.getJSONArray(checkPath[checkPath.length - 1]))) {
                return "Equivalence does hold true";
            } else
                return "Equivalence does not hold true for \n" + object.getJSONArray(checkPath[checkPath.length - 1]).toString() + " \n\n" + object2.getJSONArray(checkPath[checkPath.length - 1]).toString();
        }
        if (primaryTest.getClass() != secondaryTest.getClass()) return "responses not in same format";
        else return primaryTest.getClass().toString();
    }

    public String buildURL(String urlBase, String[] args, String[] values) { //Simple function for building a url
        String urlBackend = "";
        for (int i = 0; i < args.length; i++) {
            urlBackend += "&" + args[i] + "=" + values[i];
        }
        urlBase += urlBackend;
        return urlBase;
    }

    /**
     * This is the subset test
     *
     * @param urlBase
     * @param values
     * @param checkPath
     * @return
     */
    @GetMapping(path = "/subset")
    public @ResponseBody
    String checkSubset(@RequestParam String urlBase, @RequestParam String[] values, @RequestParam String[] checkPath, String name, String token) {
        Object primaryTest;
        ArrayList<JSONObject> secondaryTests = new ArrayList<>();
        try {
            primaryTest = getReturn(name, token, urlBase);//Get api call return as an object
            for (int i = 1; i < values.length; i++) {
                System.out.println(urlBase);
                System.out.println(values[i - 1] + " " + values[i]);
                urlBase = urlBase.replaceAll(values[i - 1], values[i]);
                System.out.println(urlBase);
                System.out.println();
                secondaryTests.add(getReturn(name, token, urlBase));
            }
            if (primaryTest.getClass() == JSONObject.class) {
                for (int i = 0; i < secondaryTests.size(); i++) { //For each secondary result
                    JSONObject object = (JSONObject) primaryTest; //Convert Object to JSONObject
                    JSONObject object2 = (JSONObject) secondaryTests.get(i);
                    for (int x = 0; x < checkPath.length - 1; x++) { //Find the checkPath
                        object = object.getJSONObject(checkPath[x]);
                        object2 = object2.getJSONObject(checkPath[x]);
                    }
                    if (testSubset(object.getJSONArray(checkPath[checkPath.length - 1]), object2.getJSONArray(checkPath[checkPath.length - 1])) == false) {
                        return "Subset test does not hold true";
                    }
                }
                return "subset test does hold true";
            }
        } catch (IllegalArgumentException e) {
            return "IllegalArgumentException";
        } catch (HttpClientErrorException e) {
            return "HttpClientErrorException" + e;
        } catch (ClassCastException e) {
            return "response is not in json format";
        }
        return "";
    }

    private boolean testSubset(JSONArray first, JSONArray second) {
        ArrayList<String> match = new ArrayList<>(); //Convert the responses to strings.
        ArrayList<String> test = new ArrayList<>();
        for (int i = 0; i < second.length(); i++) { //For each response change to string and add to ArrayList
            match.add(second.get(i).toString());
        }
        for (int i = 0; i < first.length(); i++) {
            test.add(first.get(i).toString());
        }
        System.out.println("Match size " + first.length() + " Test size " + second.length());
        if (match.size() > test.size()) return false;
        for (String s : match) {
            boolean matched = false;
            for (String ss : test) {
                if (s.equals(ss)) matched = true;
            }
            if (!matched) return false;
        }
        return true;
    }

    /**
     * This is the equality test logic for JSONArray type
     *
     * @param testArray
     * @param matchArray
     * @return
     */
    public boolean testEquality(JSONArray testArray, JSONArray matchArray) { //If array lengths do not match we can instantly return false.
        ArrayList<String> match = new ArrayList<>(); //Convert the responses to strings.
        ArrayList<String> test = new ArrayList<>();
        if (testArray.length() != matchArray.length()) return false;
        for (int i = 0; i < testArray.length(); i++) { //For each response change to string and add to ArrayList
            match.add(matchArray.get(i).toString());
            test.add(testArray.get(i).toString());
        }//TODO test to see if this gives a runtime error for removal of array item.
        while (test.size() > 0) { //While there are items to be checked continue
            if (test.size() < match.size()) return false;
            boolean matched = false;
            for (int i = 0; i < match.size(); i++) {
                if (test.get(0).equals(match.get(i)) && !matched) { //Test each response to see which one matches
                    test.remove(0);
                    match.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) return false; //If none matched current test string return false
        }
        return true; //If all test Strings matched return true.
    }

    /**
     * This is the equality test logic for ArrayList type.
     *
     * @param testArray
     * @param matchArray
     * @return
     */
    public boolean testEquality(List testArray, List matchArray) {
        if (testArray.size() != matchArray.size())
            return false;//If array lengths do not match we can instantly return false.
        ArrayList<String> match = new ArrayList<>(); //Convert the responses to strings.
        ArrayList<String> test = new ArrayList<>();
        for (int i = 0; i < testArray.size(); i++) { //For each response change to string and add to ArrayList
            match.add(matchArray.get(i).toString());
            test.add(testArray.get(i).toString());
        }
        while (test.size() > 0) { //While there are items to be checked continue
            boolean matched = false;
            for (int i = 0; i < match.size(); i++) {
                if (test.get(0).equals(match.get(i)) && !matched) { //Test each response to see which one matches
                    test.remove(0);
                    match.remove(i);
                    matched = true;
                    break;
                }
            }
            if (!matched) return false; //If none matched current test string return false
        }
        return true;//If all test Strings matched return true.
    }

    /**
     * The disjoint test expects the two result sets to be completely different from one another.
     *
     * @param urlOne
     * @param urlTwo
     * @param checkPath
     * @return
     */
    @GetMapping(path = "/disjoint")
    public @ResponseBody
    String checkDisjoint(@RequestParam String urlOne, @RequestParam String urlTwo, @RequestParam String[] checkPath, String name, String token) {
        RestTemplate restTemplate = new RestTemplate(); //Creates the rest template for API calls to RESTful apis.

        JSONObject firstResponse = getReturn(name, token, urlOne); //We get the response of the first call and second as an Object.
        JSONObject secondResponse = getReturn(name, token, urlTwo);

        //We check the type of Object the response was as we must act differently for different responses
        // If the response is a JSON object we must first find the JSONArray contaning the data to be tested if there is a check path.
        if (firstResponse.getClass() == JSONObject.class) {
            JSONObject object = (JSONObject) firstResponse; //Cast the first and second responses from Object to JSONObject type.
            JSONObject object2 = (JSONObject) secondResponse;
            for (int x = 0; x < checkPath.length - 1; x++) { //If there is a check path then we navigate through the object to the destination we want to test.
                object = object.getJSONObject(checkPath[x]);
                object2 = object2.getJSONObject(checkPath[x]);
            }
            if (testEquality(object.getJSONArray(checkPath[checkPath.length - 1]), object2.getJSONArray(checkPath[checkPath.length - 1]))) {
                return "Disjoint does not hold true"; //If the test for equality returns true then the two objects are not in disjoint.
            } else return "Disjoint does hold true"; //If true is not returned then the two objects must be in disjoint.
        } else
            return "Unknown Format type " + firstResponse.getClass(); //This will return the objects type and an error message if the response format is not known.
    }

    /**
     * This is the complete test. This checks to see that all sub queries add up to the base query
     *
     * @param urlBase
     * @param values
     * @param checkPath
     * @return
     */
    @GetMapping(path = "/complete")
    public @ResponseBody
    String checkComplete(@RequestParam String urlBase, @RequestParam String[] values, @RequestParam String[] checkPath, String name, String token) {
        RestTemplate restTemplate = new RestTemplate(); //Create the rest template for RESTful api calls

        JSONObject firstResponse = getReturn(name, token, urlBase); //Get the primary response
        ArrayList<JSONObject> secondaryResponses = new ArrayList<>();
        for (int i = 1; i < values.length; i++) { //Get the secondary responses
            urlBase = urlBase.replaceAll(values[i - 1], values[i]);
            System.out.println(values[i - 1]);
            System.out.println(values[i]);
            System.out.println(urlBase);
            secondaryResponses.add(getReturn(name, token, urlBase));
        }
        ArrayList<JSONArray> array = new ArrayList<>();

        for (int i = 0; i < checkPath.length - 1; i++) { //Find the checkpath
            firstResponse = firstResponse.getJSONObject(checkPath[i]);
        }
        JSONArray temp = firstResponse.getJSONArray(checkPath[checkPath.length - 1]);
        for (int i = 0; i < secondaryResponses.size(); i++) {
            JSONObject object = secondaryResponses.get(i);
            for (int x = 0; x < checkPath.length - 1; x++) { //Find the checkpath
                object = object.getJSONObject(checkPath[x]);
            }
            array.add(object.getJSONArray(checkPath[checkPath.length - 1]));
        }
        if (testComplete(array, temp)) {
            return "Complete does hold true";
        } else return "Complete does not hold true";

        //TODO add the path finding and test

    }

    /**
     * Logic for complete test for JSONArray type.
     *
     * @param secondaryResponses
     * @param firstResponse
     * @return
     */
    public boolean testComplete(ArrayList<JSONArray> secondaryResponses, JSONArray firstResponse) {
        ArrayList<String> match = new ArrayList<>();
        ArrayList<String> test = new ArrayList<>(); //Create two arrays of strings
        for (int i = 0; i < firstResponse.length(); i++) {
            test.add(firstResponse.get(i).toString()); //Add the first responses as strings
        }
        for (int i = 0; i < secondaryResponses.size(); i++) { //Add the secondary responses as strings
            for (int x = 0; x < secondaryResponses.get(i).length(); x++) {
                match.add(secondaryResponses.get(i).get(x).toString());
            }
        }
        while (test.size() > 0) { //Test each element to make sure they all match
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


    /**
     * This is the difference test. This checks to see if one value of return objects that are supposed to be different are indeed different.
     *
     * @param urlOne
     * @param urlTwo
     * @param checkPath
     * @return
     */
    @GetMapping(path = "/difference")
    public @ResponseBody
    String checkDifference(@RequestParam String urlOne, @RequestParam String urlTwo, @RequestParam String[] checkPath, String name, String token) {

        RestTemplate restTemplate = new RestTemplate(); //Create rest template
        JSONObject firstResponse = getReturn(name, token, urlOne); //Get our responses as objects
        JSONObject secondResponse = getReturn(name, token, urlTwo);

        try {
            JSONObject object = (JSONObject) firstResponse; //Convert the objects to JSONObjects.
            JSONObject object2 = (JSONObject) secondResponse;
            for (int i = 0; i < checkPath.length - 1; i++) { //Find the checkpath
                object = object.getJSONObject(checkPath[i]);
                object2 = object2.getJSONObject(checkPath[i]);
            }
            if (!object.get(checkPath[checkPath.length - 1]).equals(checkPath[checkPath.length - 1])) { //If the part we are checking for differnce is not the same return test held true
                return "Difference does hold true";
            } else return "Difference does not hold true"; //Else return test held false
        } catch (JSONException e) {
            return "Response not of type JSONObject"; //Need to add in more catches for different errors.
        }

    }

    @GetMapping(path = "scheduledTest")
    public @ResponseBody
    String systemCheck(@RequestParam String filePath, @RequestParam String name, @RequestParam String token) {
        String response = "";
        File file = null;
        Scanner read = null;
        try {
            file = new File(filePath);
            read = new Scanner(file);
        } catch (FileNotFoundException e) {
            return "File was not found.";
        }
        int i = 0;
        while (read.hasNext()) {
            i++;
            String line = read.nextLine();
            String[] words = line.split("-");
            switch (words[0]) {
                case "equality":
                    try {
                        String[] secondArg = {words[2]};
                        String[] thirdArg = {words[3]};
                        String ret = checkEquality(words[1], secondArg, thirdArg, name, token);
                        response += "Test " + i + " : " + ret + "\n";
                    } catch (Exception e) {
                        response += "Test " + i + " : Not enough arguments\n";
                    }
                    break;
                case "equivalence":
                    try {
                        String[] thirddArg = {words[3]};
                        String rett = checkEquivalence(words[1], words[2], thirddArg, name, token);
                        response += "Test " + i + " : " + rett + "\n";
                    } catch (Exception e) {
                        response += "Test " + i + " : Not enough arguments\n";
                    }
                    break;
                case "subset":
                    try {
                        String[] seconddArg = {words[2]};
                        String[] thirdddArg = {words[3]};
                        String rettt = checkSubset(words[1], seconddArg, thirdddArg, name, token);
                        response += "Test " + i + " : " + rettt + "\n";
                    } catch (Exception e) {
                        response += "Test " + i + " : Not enough arguments\n";
                    }
                    break;
                case "disjoint":
                    try {
                        String[] thirdddddArg = {words[3]};
                        String retttt = checkDisjoint(words[1], words[2], thirdddddArg, name, token);
                        response += "Test " + i + " : " + retttt + "\n";
                    } catch (Exception e) {
                        response += "Test " + i + " : Not enough arguments\n";
                    }
                    break;
                case "complete":
                    try {
                        String[] thirddddddArg = {words[2]};
                        String[] secondddArg = {words[3]};
                        String rettttt = checkComplete(words[1], secondddArg, thirddddddArg, name, token);
                        response += "Test " + i + " : " + rettttt + "\n";
                    } catch (Exception e) {
                        response += "Test " + i + " : Not enough arguments\n";
                    }
                    break;
                case "difference":
                    try {
                        String[] thirdddddddArg = {words[3]};
                        String retttttt = checkDifference(words[1], words[2], thirdddddddArg, name, token);
                        response += "Test " + i + " : " + retttttt + "\n";
                    } catch (NullPointerException e) {
                        response += "Test " + i + " : Not enough arguments\n";
                    }
            }
        }
        return response;
    }
}