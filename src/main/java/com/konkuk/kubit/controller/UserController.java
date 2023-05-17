package com.konkuk.kubit.controller;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }
    @GetMapping("/get")
    public Map test(){
        Map result = new HashMap<String, Object>();
        List<User> userList =  userService.getUserList();
        for(User user:userList){
            result.put(user.getId(), user.getUsername());
        }
        return result;
    }
    @PostMapping("/")
    public Long createUser(@RequestBody User user) {
        return userService.join(user);
    }
}
