package com.konkuk.kubit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth-test")
public class TestController {
    @PostMapping
    public ResponseEntity<String> authTest(Authentication authentication){
        // header에 첨부해야함
        return ResponseEntity.ok().body("userId :"+ authentication.getName());
    }
}
