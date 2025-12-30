package project7.tulipmetric.domain.Company;

import org.springframework.data.jpa.repository.JpaRepository;
import project7.tulipmetric.domain.Market.Market;

import java.util.List;

public interface CompanyRepoitory extends JpaRepository<Company,Long> {
    List<Company> findAllByMarket(String market);
}
