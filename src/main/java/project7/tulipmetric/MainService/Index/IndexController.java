package project7.tulipmetric.MainService.Index;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("market",indexService.IndexMarketLoad());
        return "/MainService/index";
    }
    @GetMapping("/industry-detail")
    public String industry_detail(){
        return "/MainService/industry-detail";
    }


}
