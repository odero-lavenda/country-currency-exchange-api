package com.stagetwo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;
@Data
public class ExchangeRateResponseDto {

    @JsonProperty("base_code")
    private String baseCode;

    @JsonProperty("time_last_update_utc")
    private String timeLastUpdateUtc;

    private Map<String, Double> rates;
}
