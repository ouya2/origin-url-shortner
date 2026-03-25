package com.origin.urlshortener.service;

import org.springframework.stereotype.Service;

@Service
public class UrlShortenerService {
    
    public String shorten(String originalUrl) {
        // Placeholder for URL shortening logic
        return "short.ly/abc123";
    }

    public String getOriginalUrl(String shortUrl) {
        // Placeholder for retrieving original URL logic
        return "https://www.example.com";
    }


}
