package com.example.ex1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
    public SampleController() {
    }

    @GetMapping({"/hello"})
    public String[] hello() {
        return new String[]{"Hello", "World"};
    }
}
