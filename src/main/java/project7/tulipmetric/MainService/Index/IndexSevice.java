package project7.tulipmetric.MainService.Index;

import org.springframework.security.oauth2.jwt.Jwt;
import project7.tulipmetric.AOP.LogExecutionTime;
import project7.tulipmetric.domain.Company.Company;
import project7.tulipmetric.domain.Market.Market;
import project7.tulipmetric.domain.WishMarket.Wishmarket;

import java.util.List;

public interface IndexSevice {
    List<Wishmarket> findWishMarketByLoginId(Jwt jwt);

    @LogExecutionTime
    List<Market> findAllMarkets();

    @LogExecutionTime
    Market findMarketById(Long id);

    @LogExecutionTime
    List<Company> findCompaniesByMarket(String market);

}
