package project7.tulipmetric;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class testController {
    @GetMapping("/community")
    public String community(){
        return "community";
    }
    @GetMapping("/discussion-detail")
    public String discussion_detail(){
        return "discussion-detail";
    }
    @GetMapping("/")
    public String index(){
        return "index";
    }
    @GetMapping("/industry-detail")
    public String industry_detail(){
        return "industry-detail";
    }
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }
}
