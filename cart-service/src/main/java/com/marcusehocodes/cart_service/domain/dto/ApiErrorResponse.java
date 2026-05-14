package com.marcusehocodes.cart_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private int status;
    private String message;
    private GenericErrorMessage errors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenericErrorMessage {
        private String message;
    }

}
