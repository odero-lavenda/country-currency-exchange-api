package com.stagetwo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StatusResponseDto {

    @JsonProperty("total_countries")
    private long totalCountries;

    @JsonProperty("last_refreshed_at")
    private String lastRefreshedAt;
}
