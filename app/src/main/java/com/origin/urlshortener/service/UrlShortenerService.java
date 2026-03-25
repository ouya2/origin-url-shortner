package com.origin.urlshortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.origin.urlshortener.repository.UrlMappingRepository;

@Service
public class UrlShortenerService {
    
    @Autowired
    private UrlMappingRepository urlMappingRepository;

    public UrlShortenerService(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    public String shorten(String originalUrl) {
        // Placeholder for URL shortening logic
        return "short.ly/abc123";
    }

    public String getOriginalUrl(String shortUrl) {
        // Placeholder for retrieving original URL logic
        return "https://www.example.com";
    }


}
