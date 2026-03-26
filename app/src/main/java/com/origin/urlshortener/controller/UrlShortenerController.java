
package com.origin.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.origin.urlshortener.model.ShortenUrlRequest;
import com.origin.urlshortener.model.ShortenUrlResponse;
import com.origin.urlshortener.model.UrlInfoResponse;
import com.origin.urlshortener.model.UrlMapping;
import com.origin.urlshortener.service.UrlShortenerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/short-urls")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(final UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping
    public ResponseEntity<ShortenUrlResponse> shortenUrl(
        @Valid @RequestBody ShortenUrlRequest request) {
        UrlMapping mapping = urlShortenerService.shorten(request.originalUrl());
        
        ShortenUrlResponse response = new ShortenUrlResponse(
            mapping.code(),
            "/r/" + mapping.code(),
            mapping.originalUrl()
        );

        return ResponseEntity.ok(response);
    }   

    @GetMapping("/{code}")
    public ResponseEntity<UrlInfoResponse> getOriginalUrl(@PathVariable String code) {
        UrlMapping mapping = urlShortenerService.getUrlInfo(code);

        UrlInfoResponse response = new UrlInfoResponse(
            mapping.code(),
            mapping.originalUrl()
        );

        return ResponseEntity.ok(response);
    }
}
