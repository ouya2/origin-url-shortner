package com.origin.urlshortener.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.origin.urlshortener.exception.InvalidUrlException;
import com.origin.urlshortener.exception.ShortCodeNotFoundException;
import com.origin.urlshortener.model.UrlMapping;
import com.origin.urlshortener.repository.UrlMappingRepository;

@Service
public class UrlShortenerService {

    private static final int MAX_SHORTEN_ATTEMPTS = 10;
    
    @Autowired
    private final UrlMappingRepository repository;

    @Autowired
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlShortenerService(final UrlMappingRepository urlMappingRepository, final ShortCodeGenerator shortCodeGenerator) {
        this.repository = urlMappingRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public UrlMapping shorten(String originalUrl) {
        validateUrl(originalUrl);
        
        return repository.findByOriginalUrl(originalUrl)
            .orElseGet(() -> createNewMapping(originalUrl));
    }

    public String getOriginalUrl(String code) {
        return repository.findByCode(code)
        .map(UrlMapping::originalUrl)
        .orElseThrow(() -> new ShortCodeNotFoundException(code));
    }
    
    public UrlMapping getUrlInfo(String code) {
        return repository.findByCode(code)
        .orElseThrow(() -> new ShortCodeNotFoundException(code));
    }   

    private UrlMapping createNewMapping(String originalUrl) {

        for (int i = 0; i < MAX_SHORTEN_ATTEMPTS; i++) {
            String code = shortCodeGenerator.generate();

            if (!repository.existsByCode(code)) {
                UrlMapping mapping = new UrlMapping(code, originalUrl);
                repository.save(mapping);
                return mapping;
            }
        }

        throw new IllegalStateException("Unable to generate a unique short code");
    }

    private void validateUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new InvalidUrlException("URL cannot be null or blank");
        }

         try {
            URI uri = URI.create(url);

            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (scheme == null || host == null) {
                throw new InvalidUrlException("URL must be absolute and include a host");
            }

            if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
                throw new InvalidUrlException("Only http and https URLs are supported");
            }
        } catch (IllegalArgumentException ex) {
            throw new InvalidUrlException("Invalid URL format");
        }
    }

}
