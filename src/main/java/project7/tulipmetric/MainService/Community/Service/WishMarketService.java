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

    @Transactional
    public void saveWishMarket(Jwt jwt, Long marketid) {
        Market market= marketRepository.findById(marketid).get();
        String loginid =  jwt.getSubject();

        wishMarketRepository.save(new Wishmarket(null,loginid,market));
    }

    @Transactional
    public void deletWishMarket(Jwt jwt, Long marketid) {
        Market market= marketRepository.findById(marketid).get();
        String loginid =  jwt.getSubject();
        wishMarketRepository.deleteByLoginidAndMarketid(loginid,market);
    }

    @Transactional
    public Boolean CheckWishMarket(Jwt jwt, Long id) { // 좋아요 이미 누른 사용자 인지 보내줌,
        if (jwt==null) {
            return false;
        }

        List<Wishmarket> wishmarkets =  wishMarketRepository.findAllByMarketid(marketRepository.findById(id).get());
        for  (Wishmarket wishmarket : wishmarkets) {
            if (wishmarket.getLoginid().equals(jwt.getSubject())) {
                return true;
            }
        }
        return false;
    }
}
