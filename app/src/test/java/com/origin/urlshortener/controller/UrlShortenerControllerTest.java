package com.origin.urlshortener.controller;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.origin.urlshortener.exception.GlobalExceptionHandler;
import com.origin.urlshortener.exception.InvalidUrlException;
import com.origin.urlshortener.exception.ShortCodeNotFoundException;
import static com.origin.urlshortener.logging.CorrelationIdFilter.CORRELATION_ID_HEADER;
import com.origin.urlshortener.model.UrlMapping;
import com.origin.urlshortener.service.UrlShortenerService;

import static net.bytebuddy.matcher.ElementMatchers.is;

@WebMvcTest(UrlShortenerController.class)
@Import(GlobalExceptionHandler.class)
public class UrlShortenerControllerTest {
  
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlShortenerService urlShortenerService;


    @Test
    @DisplayName("POST /api/short-urls returns 200 ok with shortened URL response")
    public void testExampleEndpoint() throws Exception {
        UrlMapping mapping = new UrlMapping("Ab12Xy", "https://example.com/page");

        given(urlShortenerService.shorten("https://example.com/page"))
                .willReturn(mapping);

        mockMvc.perform(post("/api/short-urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "originalUrl": "https://example.com/page"
                                }
                                """))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.code").value("Ab12Xy"))
            .andExpect(jsonPath("$.shortUrl").value("/r/Ab12Xy"))
            .andExpect(header().string(CORRELATION_ID_HEADER, not(is(emptyOrNullString()))));

        verify(urlShortenerService).shorten("https://example.com/page");
        verifyNoMoreInteractions(urlShortenerService);
           
    }

     @Test
    @DisplayName("POST /api/short-urls returns 400 when request validation fails")
    void shortenUrl_blankOriginalUrl_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/short-urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "originalUrl": ""
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(urlShortenerService);
    }

    @Test
    @DisplayName("POST /api/short-urls returns 400 when service rejects invalid URL")
    void shortenUrl_invalidUrlFromService_returnsBadRequest() throws Exception {
        given(urlShortenerService.shorten("not-a-url"))
                .willThrow(new InvalidUrlException("URL must be a valid absolute http/https URL"));

        mockMvc.perform(post("/api/short-urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "originalUrl": "not-a-url"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("INVALID_URL"))
                .andExpect(jsonPath("$.message").value("URL must be a valid absolute http/https URL"));

        verify(urlShortenerService).shorten("not-a-url");
        verifyNoMoreInteractions(urlShortenerService);
    }

    @Test
    @DisplayName("GET /api/short-urls/{code} returns 200 with URL info")
    void getUrlInfo_returnsOk() throws Exception {
        UrlMapping mapping = new UrlMapping("Ab12Xy", "https://example.com/page");

        given(urlShortenerService.getUrlInfo("Ab12Xy"))
                .willReturn(mapping);

        mockMvc.perform(get("/api/short-urls/Ab12Xy"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("Ab12Xy"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com/page"));

        verify(urlShortenerService).getUrlInfo("Ab12Xy");
        verifyNoMoreInteractions(urlShortenerService);
    }

    @Test
    @DisplayName("GET /api/short-urls/{code} returns 404 when code is missing")
    void getUrlInfo_missingCode_returnsNotFound() throws Exception {
        given(urlShortenerService.getUrlInfo("missing1"))
                .willThrow(new ShortCodeNotFoundException("missing1"));

        mockMvc.perform(get("/api/short-urls/missing1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorCode").value("SHORT_CODE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Short code not found: missing1"));

        verify(urlShortenerService).getUrlInfo("missing1");
        verifyNoMoreInteractions(urlShortenerService);
    }
}
