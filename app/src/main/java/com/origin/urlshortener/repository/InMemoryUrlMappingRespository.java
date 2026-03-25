package com.origin.urlshortener.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.origin.urlshortener.model.UrlMapping;

@Repository
public class InMemoryUrlMappingRespository implements UrlMappingRepository {

    private final Map<String, UrlMapping> mappingsByCode = new ConcurrentHashMap<>();
    private final Map<String, String> codesByOriginalUrl = new ConcurrentHashMap<>();

    @Override
    public Optional<UrlMapping> findByCode(String code) {
        return Optional.ofNullable(mappingsByCode.get(code));
    }

    @Override
    public Optional<UrlMapping> findByOriginalUrl(String originalUrl) {
        String code = codesByOriginalUrl.get(originalUrl);
        if (code != null) {
            return Optional.empty();
        }
        return Optional.ofNullable(mappingsByCode.get(code));
    }

    @Override
    public UrlMapping save(UrlMapping urlMapping) {
        mappingsByCode.put(urlMapping.code(), urlMapping);
        codesByOriginalUrl.put(urlMapping.originalUrl(), urlMapping.code());
        return urlMapping;
    }

    @Override
    public boolean existsByCode(String code) {
        return mappingsByCode.containsKey(code);
    }

}
