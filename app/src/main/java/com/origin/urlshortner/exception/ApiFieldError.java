package com.origin.urlshortner.exception;

public record ApiFieldError(
    String field,
    String message
) {}
