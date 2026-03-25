package com.origin.urlshortner;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.origin.urlshortner.controller.ExampleController;
import static com.origin.urlshortner.logging.CorrelationIdFilter.CORRELATION_ID_HEADER;

import static net.bytebuddy.matcher.ElementMatchers.is;

@WebMvcTest(ExampleController.class)
public class ExampleControllerTest {
  
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testExampleEndpoint() throws Exception {
        mockMvc.perform(get("/hello"))
           .andExpect(status().isOk())
           .andExpect(content().string("Hello, World!"))
           .andExpect(header().string(CORRELATION_ID_HEADER, not(is(emptyOrNullString()))));
           
    }

    @Test
    public void testExampleEndpointWithCorrelationId() throws Exception {
        mockMvc.perform(get("/hello").header(CORRELATION_ID_HEADER, "abc1234"))
            .andExpect(status().isOk())
            .andExpect(header().string(CORRELATION_ID_HEADER, "abc1234"));

    }

    
}
