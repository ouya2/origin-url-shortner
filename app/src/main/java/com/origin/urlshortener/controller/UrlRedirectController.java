
package com.origin.urlshortener.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.origin.urlshortener.model.UrlMapping;
import com.origin.urlshortener.service.UrlShortenerService;

@RestController
public class UrlRedirectController {

    private final UrlShortenerService urlShortenerService;

    public UrlRedirectController(final UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> getRedirect(@PathVariable String code) {
        UrlMapping mapping = urlShortenerService.getUrlInfo(code);

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(mapping.originalUrl()))
            .build();    
    }
}
