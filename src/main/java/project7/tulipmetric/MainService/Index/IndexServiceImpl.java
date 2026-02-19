package project7.tulipmetric.MainService.Index;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import project7.tulipmetric.AOP.LogExecutionTime;
import project7.tulipmetric.domain.Company.Company;
import project7.tulipmetric.domain.Company.CompanyRepoitory;
import project7.tulipmetric.domain.Market.Market;
import project7.tulipmetric.domain.Market.MarketRepository;
import project7.tulipmetric.domain.WishMarket.WishMarketRepository;
import project7.tulipmetric.domain.WishMarket.Wishmarket;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexServiceImpl implements IndexSevice{
    private final MarketRepository marketRepository;
    private final CompanyRepoitory companyRepoitory;
    private final WishMarketRepository wishMarketRepository;

    public List<Wishmarket> findWishMarketByLoginId(Jwt jwt) {
        if (jwt == null) {
            return null;
        }
        return wishMarketRepository.findAllByLoginid(jwt.getSubject());
    }

    @LogExecutionTime
    public List<Market> findAllMarkets() {
        return marketRepository.findAll();
    }

    @LogExecutionTime
    public Market findMarketById(Long id) {
        return marketRepository.findById(id).get();
    }

    @LogExecutionTime
    public List<Company> findCompaniesByMarket(String market) {
        return companyRepoitory.findAllByMarket(market);
    }

}
