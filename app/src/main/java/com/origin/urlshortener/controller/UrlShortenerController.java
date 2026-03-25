
package com.origin.urlshortener.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlShortenerController {

    // This is a placeholder for the controller logic.
    // You can add methods to handle HTTP requests here.
    @GetMapping("/{code}")
    public String getRedirect() {
        return "Hello, World!";
    }

    @PostMapping("/api/urls")
    public String shortenUrl() {
        return "Shortened URL";
    }   

    @GetMapping("/api/urls/{code}")
    public String getOriginalUrl() {
        return "Original URL";
    }   
}
