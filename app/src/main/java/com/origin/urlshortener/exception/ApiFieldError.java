package com.origin.urlshortener.exception;

public record ApiFieldError(
    String field,
    String message
) {}
