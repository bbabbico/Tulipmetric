package project7.tulipmetric.domain.WishMarket;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project7.tulipmetric.domain.Market.Market;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Wishmarket{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String loginid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="marketid")
    private Market marketid;


}
