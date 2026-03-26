package com.origin.urlshortener.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.origin.urlshortener.exception.GlobalExceptionHandler;
import com.origin.urlshortener.exception.ShortCodeNotFoundException;
import com.origin.urlshortener.model.UrlMapping;
import com.origin.urlshortener.service.UrlShortenerService;

@WebMvcTest(controllers = UrlRedirectController.class)
@Import(GlobalExceptionHandler.class)
public class UrlRedirectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlShortenerService urlShortenerService;

    @Test
    @DisplayName("GET /{code} returns 302 redirect with Location header")
    void redirect_returnsFound() throws Exception {
        UrlMapping mapping = new UrlMapping("Ab12Xy", "https://example.com/page");

        given(urlShortenerService.getUrlInfo("Ab12Xy"))
                .willReturn(mapping);

        mockMvc.perform(get("/r/Ab12Xy"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/page"));

        verify(urlShortenerService).getUrlInfo("Ab12Xy");
        verifyNoMoreInteractions(urlShortenerService);
    }

    @Test
    @DisplayName("GET /{code} returns 404 when short code does not exist")
    void redirect_missingCode_returnsNotFound() throws Exception {
        given(urlShortenerService.getUrlInfo("34jsdf"))
                .willThrow(new ShortCodeNotFoundException("34jsdf"));

        mockMvc.perform(get("/r/34jsdf"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("SHORT_CODE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Short code not found: 34jsdf"));

        verify(urlShortenerService).getUrlInfo("34jsdf");
        verifyNoMoreInteractions(urlShortenerService);
    }

    @Test
    void redirect_invalidCodeFormat_returnsNotFound() throws Exception {
        mockMvc.perform(get("/r/abc"))  // too short
                .andExpect(status().isNotFound());
     }
}
