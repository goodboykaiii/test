package com.whk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Jrebel {

    @GetMapping("/test")
    public String test() {
        System.out.println("test ok ~~~");

        return "test ok122222";
    }

}
