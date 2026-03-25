package com.origin.urlshortener.repository;

import java.util.Optional;

import com.origin.urlshortener.model.UrlMapping;

public interface UrlMappingRepository {

    Optional<UrlMapping> findByCode(String code);

    Optional<UrlMapping> findByOriginalUrl(String originalUrl);

    UrlMapping save(UrlMapping urlMapping);
    
    boolean existsByCode(String shortUrl);
}
