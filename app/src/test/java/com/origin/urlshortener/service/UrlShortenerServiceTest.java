package com.origin.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.origin.urlshortener.exception.InvalidUrlException;
import com.origin.urlshortener.exception.ShortCodeNotFoundException;
import com.origin.urlshortener.model.UrlMapping;
import com.origin.urlshortener.repository.UrlMappingRepository;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository repository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @InjectMocks
    private UrlShortenerService service;

    @Test
    void shorten_shouldCreateNewMapping_whenUrlIsValidAndNotExisting() {
        String originalUrl = "https://www.originenergy.com.au/electricity-gas/plans.html";
        String code = "a1B2c3";
        UrlMapping expected = new UrlMapping(code, originalUrl);

        when(repository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(shortCodeGenerator.generate()).thenReturn(code);
        when(repository.existsByCode(code)).thenReturn(false);
        when(repository.save(expected)).thenReturn(expected);

        UrlMapping result = service.shorten(originalUrl);

        assertThat(result).isEqualTo(expected);
        verify(repository).findByOriginalUrl(originalUrl);
        verify(shortCodeGenerator).generate();
        verify(repository).existsByCode(code);
        verify(repository).save(expected);
    }

    @Test
    void shorten_shouldReturnExistingMapping_whenUrlAlreadyShortened() {
        String originalUrl = "https://www.originenergy.com.au/electricity-gas/plans.html";
        UrlMapping existing = new UrlMapping("a1B2c3", originalUrl);

        when(repository.findByOriginalUrl(originalUrl)).thenReturn(Optional.of(existing));

        UrlMapping result = service.shorten(originalUrl);

        assertThat(result).isEqualTo(existing);
        verify(repository).findByOriginalUrl(originalUrl);
        verifyNoInteractions(shortCodeGenerator);
        verify(repository, never()).save(any());
    }
    
    @Test
    void shorten_shouldThrowInvalidUrlException_whenUrlIsBlank() {
        assertThatThrownBy(() -> service.shorten(" "))
                .isInstanceOf(InvalidUrlException.class)
                .hasMessageContaining("blank");

        verifyNoInteractions(repository, shortCodeGenerator);
    }

    @Test
    void shorten_shouldThrowInvalidUrlException_whenUrlFormatIsInvalid() {
        assertThatThrownBy(() -> service.shorten("not-a-valid-url"))
                .isInstanceOf(InvalidUrlException.class);

        verifyNoInteractions(repository, shortCodeGenerator);
    }

    @Test
    void shorten_shouldThrowInvalidUrlException_whenSchemeIsUnsupported() {
        assertThatThrownBy(() -> service.shorten("ftp://example.com/file"))
                .isInstanceOf(InvalidUrlException.class)
                .hasMessageContaining("http");

        verifyNoInteractions(repository, shortCodeGenerator);
    }

    @Test
    void shorten_shouldRetry_whenGeneratedCodeAlreadyExists() {
        String originalUrl = "https://www.originenergy.com.au/electricity-gas/plans.html";
        String firstCode = "a1B2c3";
        String secondCode = "d4E5f6";
        UrlMapping expected = new UrlMapping(secondCode, originalUrl);

        when(repository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(shortCodeGenerator.generate()).thenReturn(firstCode, secondCode);
        when(repository.existsByCode(firstCode)).thenReturn(true);
        when(repository.existsByCode(secondCode)).thenReturn(false);
        when(repository.save(expected)).thenReturn(expected);

        UrlMapping result = service.shorten(originalUrl);

        assertThat(result).isEqualTo(expected);
        verify(shortCodeGenerator, times(2)).generate();
        verify(repository).save(expected);
    }

    @Test
    void shorten_shouldThrowException_whenUniqueCodeCannotBeGenerated() {
        String originalUrl = "https://www.originenergy.com.au/electricity-gas/plans.html";
        String repeatedCode = "a1B2c3";

        when(repository.findByOriginalUrl(originalUrl)).thenReturn(Optional.empty());
        when(shortCodeGenerator.generate()).thenReturn(
                repeatedCode, repeatedCode, repeatedCode, repeatedCode, repeatedCode,
                repeatedCode, repeatedCode, repeatedCode, repeatedCode, repeatedCode
        );
        when(repository.existsByCode(repeatedCode)).thenReturn(true);

        assertThatThrownBy(() -> service.shorten(originalUrl))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("unique short code");

        verify(shortCodeGenerator, times(10)).generate();
        verify(repository, never()).save(any());
    }

    @Test
    void getOriginalUrl_shouldReturnOriginalUrl_whenCodeExists() {
        String code = "a1B2c3";
        String originalUrl = "https://www.originenergy.com.au/electricity-gas/plans.html";
        UrlMapping mapping = new UrlMapping(code, originalUrl);

        when(repository.findByCode(code)).thenReturn(Optional.of(mapping));

        String result = service.getOriginalUrl(code);

        assertThat(result).isEqualTo(originalUrl);
    }

    @Test
    void getOriginalUrl_shouldThrowNotFound_whenCodeDoesNotExist() {
        String code = "missing1";

        when(repository.findByCode(code)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getOriginalUrl(code))
                .isInstanceOf(ShortCodeNotFoundException.class)
                .hasMessageContaining(code);
    }

    @Test
    void getUrlInfo_shouldReturnMapping_whenCodeExists() {
        String code = "a1B2c3";
        UrlMapping mapping = new UrlMapping(code, "https://www.originenergy.com.au/electricity-gas/plans.html");

        when(repository.findByCode(code)).thenReturn(Optional.of(mapping));

        UrlMapping result = service.getUrlInfo(code);

        assertThat(result).isEqualTo(mapping);
    }

    @Test
    void shorten_shouldThrowInvalidUrlException_whenUrlIsNull() {
        assertThatThrownBy(() -> service.shorten(null))
                .isInstanceOf(InvalidUrlException.class);

        verifyNoInteractions(repository, shortCodeGenerator);
    }

    @Test
    void getUrlInfo_shouldThrowNotFound_whenCodeDoesNotExist() {
        String code = "missing1";

        when(repository.findByCode(code)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUrlInfo(code))
                .isInstanceOf(ShortCodeNotFoundException.class)
                .hasMessageContaining(code);
    }

    @Test
    void shorten_shouldThrowInvalidUrlException_whenHostIsMissing() {
        String invalidUrl = "https:///path";

        assertThatThrownBy(() -> service.shorten(invalidUrl))
                .isInstanceOf(InvalidUrlException.class);

        verifyNoInteractions(repository, shortCodeGenerator);
    }

    @Test
    void shorten_shouldAcceptUrlWithQueryAndFragment() {
        String url = "https://example.com/path?x=1#section";
        String code = "a1B2c3";
        UrlMapping expected = new UrlMapping(code, url);

        when(repository.findByOriginalUrl(url)).thenReturn(Optional.empty());
        when(shortCodeGenerator.generate()).thenReturn(code);
        when(repository.existsByCode(code)).thenReturn(false);
        when(repository.save(expected)).thenReturn(expected);

        UrlMapping result = service.shorten(url);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shorten_shouldAcceptMixedCaseHttpScheme() {
        String url = "HTTPS://example.com/path";
        String code = "a1B2c3";
        UrlMapping expected = new UrlMapping(code, url);

        when(repository.findByOriginalUrl(url)).thenReturn(Optional.empty());
        when(shortCodeGenerator.generate()).thenReturn(code);
        when(repository.existsByCode(code)).thenReturn(false);
        when(repository.save(expected)).thenReturn(expected);

        UrlMapping result = service.shorten(url);

        assertThat(result).isEqualTo(expected);
    }
}
