package com.marcusehocodes.cart_service.exceptions;

import com.marcusehocodes.cart_service.domain.dto.ApiErrorResponse;
import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    public ApiErrorResponse.GenericErrorMessage fieldErrors;

    public UserNotFoundException(String message) {
        super(message);
        this.fieldErrors = ApiErrorResponse.GenericErrorMessage.builder()
                .message(message)
                .build();
    }
}