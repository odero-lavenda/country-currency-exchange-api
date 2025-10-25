package com.stagetwo.service;

import com.stagetwo.dto.CountryResponseDto;
import com.stagetwo.dto.ExchangeRateResponseDto;
import com.stagetwo.dto.StatusResponseDto;
import com.stagetwo.entity.Country;
import com.stagetwo.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {
    private final CountryRepository countryRepository;
    private final RestTemplate restTemplate;
    private final ImageGenerationService imageGenerationService;

    private static final String COUNTRIES_API = "https://restcountries.com/v2/all?fields=name,capital,region,population,flag,currencies";
    private static final String EXCHANGE_RATE_API = "https://open.er-api.com/v6/latest/USD";
    private static final Random random = new Random();

    @Transactional
    public void refreshCountries() {
        try {
            // Fetch countries data
            CountryResponseDto[] countries = restTemplate.getForObject(COUNTRIES_API, CountryResponseDto[].class);
            if (countries == null || countries.length == 0) {
                throw new RuntimeException("Could not fetch data from RestCountries API");
            }

            // Fetch exchange rates
            ExchangeRateResponseDto exchangeRates = restTemplate.getForObject(EXCHANGE_RATE_API, ExchangeRateResponseDto.class);
            if (exchangeRates == null || exchangeRates.getRates() == null) {
                throw new RuntimeException("Could not fetch data from Exchange Rate API");
            }

            LocalDateTime now = LocalDateTime.now();
            Map<String, Double> rates = exchangeRates.getRates();

            // Process each country
            for (CountryResponseDto dto : countries) {
                Country country = new Country();

                // Basic fields
                country.setName(dto.getName());
                country.setCapital(dto.getCapital());
                country.setRegion(dto.getRegion());
                country.setPopulation(dto.getPopulation());
                country.setFlagUrl(dto.getFlag());
                country.setLastRefreshedAt(now);

                // Currency handling
                if (dto.getCurrencies() != null && !dto.getCurrencies().isEmpty()) {
                    String currencyCode = dto.getCurrencies().get(0).getCode();
                    country.setCurrencyCode(currencyCode);

                    // Get exchange rate
                    Double exchangeRate = rates.get(currencyCode);
                    country.setExchangeRate(exchangeRate);

                    // Calculate estimated GDP
                    if (exchangeRate != null && exchangeRate > 0) {
                        double randomMultiplier = 1000 + random.nextDouble() * 1000; // 1000-2000
                        double estimatedGdp = (dto.getPopulation() * randomMultiplier) / exchangeRate;
                        country.setEstimatedGdp(estimatedGdp);
                    } else {
                        country.setEstimatedGdp(null);
                    }
                } else {
                    // No currency
                    country.setCurrencyCode(null);
                    country.setExchangeRate(null);
                    country.setEstimatedGdp(0.0);
                }

                // Update or insert
                Optional<Country> existing = countryRepository.findByNameIgnoreCase(dto.getName());
                if (existing.isPresent()) {
                    country.setId(existing.get().getId());
                }
                countryRepository.save(country);
            }

            // Generate summary image
            long totalCountries = countryRepository.count();
            List<Country> topCountries = getTopCountriesByGdp(5);
            imageGenerationService.generateSummaryImage(totalCountries, topCountries, now);

            log.info("Successfully refreshed {} countries", totalCountries);
        } catch (Exception e) {
            log.error("Error refreshing countries", e);
            throw new RuntimeException("External data source unavailable: " + e.getMessage());
        }
    }

    public List<Country> getAllCountries(String region, String currency, String sort) {
        List<Country> countries;

        // Apply filters
        if (region != null && !region.isEmpty()) {
            countries = countryRepository.findByRegionIgnoreCase(region);
        } else if (currency != null && !currency.isEmpty()) {
            countries = countryRepository.findByCurrencyCodeIgnoreCase(currency);
        } else {
            countries = countryRepository.findAll();
        }

        // Apply sorting
        if (sort != null && !sort.isEmpty()) {
            if (sort.equalsIgnoreCase("gdp_desc")) {
                countries = countries.stream()
                        .sorted((c1, c2) -> {
                            Double gdp1 = c1.getEstimatedGdp() != null ? c1.getEstimatedGdp() : 0.0;
                            Double gdp2 = c2.getEstimatedGdp() != null ? c2.getEstimatedGdp() : 0.0;
                            return Double.compare(gdp2, gdp1);
                        })
                        .collect(Collectors.toList());
            } else if (sort.equalsIgnoreCase("gdp_asc")) {
                countries = countries.stream()
                        .sorted((c1, c2) -> {
                            Double gdp1 = c1.getEstimatedGdp() != null ? c1.getEstimatedGdp() : 0.0;
                            Double gdp2 = c2.getEstimatedGdp() != null ? c2.getEstimatedGdp() : 0.0;
                            return Double.compare(gdp1, gdp2);
                        })
                        .collect(Collectors.toList());
            }
        }

        return countries;
    }

    public Country getCountryByName(String name) {
        return countryRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new NoSuchElementException("Country not found"));
    }

    @Transactional
    public void deleteCountry(String name) {
        if (!countryRepository.existsByNameIgnoreCase(name)) {
            throw new NoSuchElementException("Country not found");
        }
        countryRepository.deleteByNameIgnoreCase(name);
    }

    public StatusResponseDto getStatus() {
        StatusResponseDto response = new StatusResponseDto();
        response.setTotalCountries(countryRepository.count());

        LocalDateTime lastRefresh = countryRepository.findLatestRefreshTime();
        if (lastRefresh != null) {
            response.setLastRefreshedAt(lastRefresh.format(DateTimeFormatter.ISO_DATE_TIME));
        }

        return response;
    }

    private List<Country> getTopCountriesByGdp(int limit) {
        return countryRepository.findAll().stream()
                .filter(c -> c.getEstimatedGdp() != null && c.getEstimatedGdp() > 0)
                .sorted((c1, c2) -> Double.compare(c2.getEstimatedGdp(), c1.getEstimatedGdp()))
                .limit(limit)
                .collect(Collectors.toList());
    }


}
