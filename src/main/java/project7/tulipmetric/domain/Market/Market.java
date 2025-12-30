package project7.tulipmetric.domain.Market;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Market {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45, nullable = false)
    private String name;

    @Column(nullable = false)
    private Long totalmarketcap;

    @Column(nullable = false)
    private int marketper;

    @Column(nullable = false)
    private int stockcount;

    @Column(length = 100,nullable = false)
    private String chart; //12 개월 코스피 지수 변동.

    @Column(nullable = false)
    private int growthRate30d;

    @Column(length = 20,nullable = false)
    private String marketStatus;    // crashed/declining/stable/growing/overvalued

    @Column(nullable = false)
    private boolean trending;       // 급상승 여부

    @Column(nullable = false)
    private String description;     // 카드 설명(없으면 기본 문구)
}
