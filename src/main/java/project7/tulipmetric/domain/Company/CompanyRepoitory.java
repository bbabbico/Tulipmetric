package project7.tulipmetric.domain.Company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepoitory extends JpaRepository<Company,Long> {
    List<Company> findAllByMarket(String market);
}
