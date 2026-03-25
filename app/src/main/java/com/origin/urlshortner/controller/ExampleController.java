
package com.origin.urlshortner.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {

    // This is a placeholder for the controller logic.
    // You can add methods to handle HTTP requests here.
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }
}
