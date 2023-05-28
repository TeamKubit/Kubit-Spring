package com.konkuk.kubit.controller;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.utils.GetUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth-test")
public class TestController {
    @PostMapping
    public ResponseEntity<String> authTest(@GetUser User user){
        // header에 첨부해야함
        return ResponseEntity.ok().body("username :"+ user.getUsername());
    }
}
