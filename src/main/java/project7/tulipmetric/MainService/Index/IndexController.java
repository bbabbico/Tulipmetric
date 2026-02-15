package project7.tulipmetric.MainService.Index;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project7.tulipmetric.Mypage.WishMarketService;
import project7.tulipmetric.domain.Market.Market;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final IndexService indexService;
    private final WishMarketService wishMarketService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal Jwt jwt, Model model) {
        model.addAttribute("wishlist", indexService.findWishMarketByLoginId(jwt));
        model.addAttribute("market", indexService.findAllMarkets());
        return "MainService/index";
    }

    @GetMapping("/industry-detail")
    public String industry_detail(@AuthenticationPrincipal Jwt jwt, @RequestParam Long id, Model model) {

        Boolean check = jwt != null && wishMarketService.checkWishMarket(jwt, id); //왼쪽 조건인 jwt != null 이 거짓이면 오른쪽 함수는 실행되지 않음. && and 에 따라 어짜피 거짓이라
        Market market = indexService.findMarketById(id);
        if (check) {
            model.addAttribute("wishcheck", true);
        }

        model.addAttribute("market", market);
        model.addAttribute("company", indexService.findCompaniesByMarket(market.getName()));
        return "MainService/industry-detail";
    }

}
