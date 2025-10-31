package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/")
    public HelloMessage sayHello() {
        return new HelloMessage("Hello, World!");
    }

    public record HelloMessage(String message) { }
}
