package project7.tulipmetric.domain.WishMarket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishMarketRepository extends JpaRepository<Wishmarket, Long> {
    List<Wishmarket> findAllByLoginid(String loginid);
}
