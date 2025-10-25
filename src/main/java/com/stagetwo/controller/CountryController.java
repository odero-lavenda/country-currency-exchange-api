package com.stagetwo.controller;


import com.stagetwo.dto.ErrorResponseDto;
import com.stagetwo.entity.Country;
import com.stagetwo.service.CountryService;
import com.stagetwo.service.ImageGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;
    private final ImageGenerationService imageGenerationService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshCountries() {
        try {
            countryService.refreshCountries();
            return ResponseEntity.ok(Map.of("message", "Countries refreshed successfully"));
        } catch (Exception e) {
            String apiName = e.getMessage().contains("RestCountries") ? "RestCountries API" : "Exchange Rate API";
            ErrorResponseDto error = new ErrorResponseDto(
                    "External data source unavailable",
                    "Could not fetch data from " + apiName
            );
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<List<Country>> getAllCountries(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String sort) {
        List<Country> countries = countryService.getAllCountries(region, currency, sort);
        return ResponseEntity.ok(countries);
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> getCountryByName(@PathVariable String name) {
        try {
            Country country = countryService.getCountryByName(name);
            return ResponseEntity.ok(country);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDto("Country not found"));
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteCountry(@PathVariable String name) {
        try {
            countryService.deleteCountry(name);
            return ResponseEntity.ok(Map.of("message", "Country deleted successfully"));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDto("Country not found"));
        }
    }

    @GetMapping("/image")
    public ResponseEntity<?> getSummaryImage() {
        File imageFile = imageGenerationService.getSummaryImage();
        if (imageFile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDto("Summary image not found"));
        }

        Resource resource = new FileSystemResource(imageFile);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }
}
