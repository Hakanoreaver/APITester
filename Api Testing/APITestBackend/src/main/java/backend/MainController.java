package backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import org.json.JSONArray;




@Controller    // This means that this class is a Controller
@RequestMapping(path="/backend") // This means URL's start with /demo (after Application path)
public class MainController {
    @GetMapping(path="/test")
    public @ResponseBody String sendTest () {
        return "System Running";
    }
}
