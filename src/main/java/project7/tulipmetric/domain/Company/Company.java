package project7.tulipmetric.domain.Company;

import jakarta.persistence.*;

@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45,nullable = false)
    private String itmsnm;

    @Column(nullable = false)
    private int clpr;

    @Column(nullable = false)
    private int vs;

    @Column(nullable = false)
    private double fltrt;

    @Column(nullable = false)
    private int trqu;

    @Column(nullable = false)
    private Long mrkttotamt;

    @Column(length = 20,nullable = false)
    private String market;


}
