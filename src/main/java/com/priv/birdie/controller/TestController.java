package com.priv.birdie.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public String test(@RequestParam("test") String context){
        return passwordEncoder.encode(context);
    }

    @GetMapping("/matching")
    public boolean match(@RequestParam("hash") String encoded,
                         @RequestParam("raw") String pass){
        return passwordEncoder.matches(pass,encoded);
    }

}
