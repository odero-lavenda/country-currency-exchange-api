package com.stagetwo.dto;

import lombok.Data;

import java.util.Currency;
import java.util.List;

@Data
public class CountryResponseDto {

    private String name;
    private String capital;
    private String region;
    private Long population;
    private String flag;
    private List<Currency> currencies;


    @Data
    public static class Currency {
        private String code;
        private String name;
        private String symbol;
    }
}
