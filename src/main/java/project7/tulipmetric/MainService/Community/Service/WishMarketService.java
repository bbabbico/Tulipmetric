package project7.tulipmetric.MainService.Community.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Market.Market;
import project7.tulipmetric.domain.Market.MarketRepository;
import project7.tulipmetric.domain.WishMarket.WishMarketRepository;
import project7.tulipmetric.domain.WishMarket.Wishmarket;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishMarketService {
    private final MarketRepository marketRepository;
    private final WishMarketRepository wishMarketRepository;

    public List<Wishmarket> findAllByLoginid(String loginid) {
        return wishMarketRepository.findAllByLoginid(loginid);
    }

    @Transactional
    public void saveWishMarket(Jwt jwt, Long marketid) {
        Market market= marketRepository.findById(marketid).get();
        String loginid =  jwt.getSubject();

        wishMarketRepository.save(new Wishmarket(null,loginid,market));
    }
}
