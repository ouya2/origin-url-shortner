package com.origin.urlshortener.model;

public record UrlMapping(
    String code, 
    String originalUrl
) {}
