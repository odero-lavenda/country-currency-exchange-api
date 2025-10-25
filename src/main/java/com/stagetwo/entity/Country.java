package com.stagetwo.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "countries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, unique = true)
    private String name;

    private String capital;

    private String region;

    @NotNull(message = "Population is required")
    @Column(nullable = false)
    private Long population;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "exchange_rate")
    private Double exchangeRate;

    @Column(name = "estimated_gdp")
    private Double estimatedGdp;

    @Column(name = "flag_url")
    private String flagUrl;

    @Column(name = "last_refreshed_at")
    private LocalDateTime lastRefreshedAt;
}
