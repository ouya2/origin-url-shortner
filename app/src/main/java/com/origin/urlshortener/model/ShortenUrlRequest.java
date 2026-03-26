package com.origin.urlshortener.model;

import jakarta.validation.constraints.NotBlank;

public record ShortenUrlRequest(
    @NotBlank(message = "Original URL must not be blank")
    String originalUrl
) {}
