package com.origin.urlshortener.controller;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.origin.urlshortener.logging.CorrelationIdFilter.CORRELATION_ID_HEADER;
import com.origin.urlshortener.service.UrlShortenerService;

import static net.bytebuddy.matcher.ElementMatchers.is;

@WebMvcTest(UrlShortenerController.class)
public class UrlShortenerControllerTest {
  
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlShortenerService urlShortenerService;


    @Test
    public void testExampleEndpoint() throws Exception {
        mockMvc.perform(get("/code"))
           .andExpect(status().isOk())
           .andExpect(content().string("Hello, World!"))
           .andExpect(header().string(CORRELATION_ID_HEADER, not(is(emptyOrNullString()))));
           
    }

    @Test
    public void testExampleEndpointWithCorrelationId() throws Exception {
        mockMvc.perform(get("/code").header(CORRELATION_ID_HEADER, "abc1234"))
            .andExpect(status().isOk())
            .andExpect(header().string(CORRELATION_ID_HEADER, "abc1234"));

    }

    
}
