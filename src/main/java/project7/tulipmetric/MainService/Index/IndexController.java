package project7.tulipmetric.MainService.Index;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project7.tulipmetric.domain.Market.Market;

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
    public String industry_detail(@RequestParam Long id,Model model) {
        Market market = indexService.IndexMarketLoadById(id);
        model.addAttribute("market",market);
        model.addAttribute("company",indexService.IndexCompanyFindByMarketLoad(market.getName()));
        return "/MainService/industry-detail";
    }


}
