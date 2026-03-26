package com.origin.urlshortener.exception;

import java.util.List;

public record ValidationErrorResponse(
    String code, 
    String message, 
    List<String> details
) {}
