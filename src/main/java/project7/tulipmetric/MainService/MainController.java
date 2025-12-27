package project7.tulipmetric.MainService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String index(){
        return "/MainService/index";
    }
    @GetMapping("/industry-detail")
    public String industry_detail(){
        return "/MainService/industry-detail";
    }


}
