package project7.tulipmetric.MainService.Index;

import org.springframework.security.oauth2.jwt.Jwt;
import project7.tulipmetric.domain.Company.Company;
import project7.tulipmetric.domain.Market.Market;
import project7.tulipmetric.domain.WishMarket.Wishmarket;

import java.util.List;

public interface IndexSevice {
    List<Wishmarket> findWishMarketByLoginId(Jwt jwt);

    List<Market> findAllMarkets();

    Market findMarketById(Long id);

    List<Company> findCompaniesByMarket(String market);

}
