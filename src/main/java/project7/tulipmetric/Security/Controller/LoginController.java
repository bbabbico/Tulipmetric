package project7.tulipmetric.Security.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String Getlogin() {
        return "Login/login";
    }
}
