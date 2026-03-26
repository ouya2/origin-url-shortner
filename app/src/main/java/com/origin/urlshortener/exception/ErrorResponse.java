package com.origin.urlshortener.exception;

public record ErrorResponse(
    String errorCode, 
    String message
) {}
