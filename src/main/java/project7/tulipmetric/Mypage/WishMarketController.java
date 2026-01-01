package project7.tulipmetric.Mypage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project7.tulipmetric.MainService.Community.Service.WishMarketService;

@RestController
@RequiredArgsConstructor
public class WishMarketController {

    private final WishMarketService wishMarketService;

    @PostMapping("/savewishmarket")
    public ResponseEntity<Integer> saveWishMarket(@AuthenticationPrincipal Jwt jwt, @RequestParam Long id) {
        wishMarketService.saveWishMarket(jwt,id);
        return new ResponseEntity<>(0, HttpStatus.OK);
    }
}
