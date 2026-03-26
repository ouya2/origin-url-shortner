package com.origin.urlshortener.model;

public record UrlInfoResponse(
    String code, 
    String originalUrl
) {}
