package project7.tulipmetric.domain.WishMarket;

import org.springframework.data.jpa.repository.JpaRepository;
import project7.tulipmetric.domain.Market.Market;

import java.util.Optional;
import java.util.List;

public interface WishMarketRepository extends JpaRepository<Wishmarket, Long> {
    List<Wishmarket> findAllByLoginid(String loginid);
    List<Wishmarket> findAllByMarketid(Market marketid);
    Optional<Wishmarket> findByLoginidAndMarketid(String loginid, Market marketid);
    boolean existsByLoginidAndMarketid(String loginid, Market marketid);
}
