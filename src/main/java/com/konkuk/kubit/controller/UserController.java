package com.konkuk.kubit.controller;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.Wallet;
import com.konkuk.kubit.domain.dto.*;
import com.konkuk.kubit.service.UserService;
import com.konkuk.kubit.utils.GetUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> createUser(@RequestBody @Valid final UserJoinRequest userDto) {
        Long id = userService.join(userDto.getUserId(), userDto.getUsername(), userDto.getPassword()); //여기서 에러나면 ExceptionHandler가 처리할 것임
        ResultResponse data = ResultResponse.builder()
                .result_code(201)
                .result_msg("회원 가입 성공 : " + id)
                .detail(id)
                .build();
        return new ResponseEntity(data, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid final UserLoginRequest dto) {
        TokenInfo tokenInfo = userService.login(dto.getUserId(), dto.getPassword());
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg("로그인 성공")
                .detail(tokenInfo)
                .build();
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenInfo> refresh(@RequestBody RefreshTokenRequest dto) {
        System.out.println(dto);
        System.out.println(dto.getRefreshToken());
        TokenInfo tokenInfo = userService.regenerateToken(dto.getRefreshToken());
        return ResponseEntity.ok().body(tokenInfo);
    }

    @GetMapping("/wallet_overall")
    public ResponseEntity<?> walletOverall(@GetUser User user){
        List<WalletDto> wallets = userService.getWalletOverall(user);
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg("지갑 정보")
                .detail(wallets)
                .build();
        return ResponseEntity.ok().body(data);
    }
}
