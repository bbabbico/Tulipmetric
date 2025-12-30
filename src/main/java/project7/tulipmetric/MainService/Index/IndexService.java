package project7.tulipmetric.MainService.Index;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project7.tulipmetric.domain.Company.Company;
import project7.tulipmetric.domain.Company.CompanyRepoitory;
import project7.tulipmetric.domain.Market.Market;
import project7.tulipmetric.domain.Market.MarketRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexService {
    private final MarketRepository marketRepository;
    private final CompanyRepoitory companyRepoitory;

    public List<Market> IndexMarketLoad(){
        return marketRepository.findAll();
    }

    public List<Company> IndexCompanyFindByMarketLoad(String market){
        return companyRepoitory.findAllByMarket(market);
    }


}
