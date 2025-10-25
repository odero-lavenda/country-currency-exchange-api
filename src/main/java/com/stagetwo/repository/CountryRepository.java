package com.stagetwo.repository;

import com.stagetwo.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByNameIgnoreCase(String name);

    List<Country> findByRegionIgnoreCase(String region);

    List<Country> findByCurrencyCodeIgnoreCase(String currencyCode);

    @Query("SELECT MAX(c.lastRefreshedAt) FROM Country c")
    LocalDateTime findLatestRefreshTime();

    void deleteByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

}
