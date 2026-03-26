package com.origin.urlshortener.model;

public record ShortenUrlResponse(
    String code, 
    String shortUrl, 
    String originalUrl
) {}
