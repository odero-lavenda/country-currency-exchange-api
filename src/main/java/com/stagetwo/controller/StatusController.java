package com.stagetwo.controller;

import com.stagetwo.dto.StatusResponseDto;
import com.stagetwo.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StatusController {
    private final CountryService countryService;

    @GetMapping("/status")
    public ResponseEntity<StatusResponseDto> getStatus() {
        StatusResponseDto status = countryService.getStatus();
        return ResponseEntity.ok(status);
    }
}
