package com.stagetwo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponseDto {
    private String error;
    private Object details;

    public ErrorResponseDto(String error) {
        this.error = error;
    }

    public ErrorResponseDto(String error, Object details) {
        this.error = error;
        this.details = details;
    }

}
