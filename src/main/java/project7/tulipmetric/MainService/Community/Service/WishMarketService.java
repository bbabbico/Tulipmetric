package project7.tulipmetric.MainService.Community.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import project7.tulipmetric.domain.Market.Market;
import project7.tulipmetric.domain.Market.MarketRepository;
import project7.tulipmetric.domain.WishMarket.WishMarketRepository;
import project7.tulipmetric.domain.WishMarket.Wishmarket;

@Service
@RequiredArgsConstructor
public class WishMarketService {
    private final MarketRepository marketRepository;
    private final WishMarketRepository wishMarketRepository;

    @Transactional
    public void saveWishMarket(Jwt jwt, Long marketid) {
        Jwt authenticatedJwt = requireJwt(jwt);
        Market market = findMarketOrThrow(marketid);
        String loginid = authenticatedJwt.getSubject();

        if (wishMarketRepository.existsByLoginidAndMarketid(loginid, market)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wish market already exists");
        }

        wishMarketRepository.save(new Wishmarket(null, loginid, market));
    }

    @Transactional
    public void deletWishMarket(Jwt jwt, Long marketid) {
        Jwt authenticatedJwt = requireJwt(jwt);
        Market market = findMarketOrThrow(marketid);
        String loginid = authenticatedJwt.getSubject();

        Wishmarket existingWish = wishMarketRepository.findByLoginidAndMarketid(loginid, market)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wish market not found"));
        wishMarketRepository.delete(existingWish);
    }

    @Transactional
    public Boolean CheckWishMarket(Jwt jwt, Long id) { // 좋아요 이미 누른 사용자 인지 보내줌,
        Jwt authenticatedJwt = requireJwt(jwt);
        Market market = findMarketOrThrow(id);

        return wishMarketRepository.existsByLoginidAndMarketid(authenticatedJwt.getSubject(), market);
    }

    private Jwt requireJwt(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authentication token");
        }
        return jwt;
    }

    private Market findMarketOrThrow(Long marketid) {
        return marketRepository.findById(marketid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Market not found"));
    }
}
