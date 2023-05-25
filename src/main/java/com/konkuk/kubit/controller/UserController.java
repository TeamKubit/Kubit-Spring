package com.konkuk.kubit.controller;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.dto.RefreshTokenRequest;
import com.konkuk.kubit.domain.dto.TokenInfo;
import com.konkuk.kubit.domain.dto.UserJoinRequest;
import com.konkuk.kubit.domain.dto.UserLoginRequest;
import com.konkuk.kubit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get")
    public List<Map> test() {
        List<Map> result = new ArrayList<>();
        List<User> userList = userService.getUserList();
        for (User user : userList) {
            Map userMap = new HashMap<String, Object>();
            userMap.put("uid", user.getUId());
            userMap.put("username", user.getUsername());
            userMap.put("userId", user.getUserId());
            result.add(userMap);
        }
        return result;
    }

    @PostMapping("/join")
    public ResponseEntity<String> createUser(@RequestBody @Valid final UserJoinRequest userDto) {
        Long id = userService.join(userDto.getUserId(), userDto.getUsername(), userDto.getPassword()); //여기서 에러나면 ExceptionHandler가 처리할 것임
        return ResponseEntity.ok().body("회원 가입 완료 : "+ id);
    }
    @PostMapping("/login")
    public ResponseEntity<TokenInfo> loginUser(@RequestBody @Valid final UserLoginRequest dto){
        TokenInfo tokenInfo = userService.login(dto.getUserId(), dto.getPassword());
        return ResponseEntity.ok().body(tokenInfo);
    }
    @PostMapping("/refresh")
    public ResponseEntity<TokenInfo> refresh(@RequestBody RefreshTokenRequest dto){
        System.out.println(dto);
        System.out.println(dto.getRefreshToken());
        TokenInfo tokenInfo = userService.regenerateToken(dto.getRefreshToken());
        return ResponseEntity.ok().body(tokenInfo);
    }
}
