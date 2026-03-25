package com.origin.urlshortener.exception;

import java.time.Instant;
import java.util.List;


public record ApiError(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    String correlatioinId,
    List<ApiFieldError> fieldErrors
) {
   
    public static ApiError of(
        int status,
        String error,
        String message,
        String path,
        String correlatioinId,
        List<ApiFieldError> fieldErrors
    ) {
        return new ApiError(Instant.now(), status, error, message, path, correlatioinId, fieldErrors);
    }

}
