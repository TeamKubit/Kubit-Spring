package com.konkuk.kubit.service;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.Wallet;
import com.konkuk.kubit.domain.dto.TokenInfo;
import com.konkuk.kubit.domain.dto.WalletDto;
import com.konkuk.kubit.event.UserCreatedEvent;
import com.konkuk.kubit.exception.AppException;
import com.konkuk.kubit.exception.ErrorCode;
import com.konkuk.kubit.repository.UserRepository;
import com.konkuk.kubit.utils.GetUser;
import com.konkuk.kubit.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
//@RequiredArgsConstructor // 어노테이션을 통한 생성자 주입
//@Component
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final ApplicationEventPublisher eventPublisher;


    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.eventPublisher = eventPublisher;
    }

    @Value("${jwt.token.secret}") // from application.properties
    private String key;
    private Long accessTokenExpireTimeMs = 1000 * 60 * 60L; // 1hour
    private Long refreshTokenExpireTimeMs = 1000 * 60 * 60 * 24 * 7L; //7 day

    public List<User> getUserList() {
        return userRepository.findAll();
    }

    public Long join(String userId, String username, String pw) {
        // id 중복 체크
        userRepository.findByUserId(userId)
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERID_DUPLICATED, userId + "가 이미 존재합니다.");
                });
        // 저장
        User user = User.builder()
                .userId(userId)
                .username(username)
                .pw(encoder.encode(pw)) // 비밀번호 해싱
                .money(10000000)
                .depositCount(0)
                .build();
        userRepository.save(user);
        UserCreatedEvent event = new UserCreatedEvent(user);
        eventPublisher.publishEvent(event);
        return user.getUId();
    }

    public Map login(String userId, String pw) {
        // userId 없음
        User selectedUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USERID_NOTFOUND, userId + "에 해당하는 사용자가 없습니다")); //user 없을 때 appexception throw
        // password 틀림
        if (!encoder.matches(pw, selectedUser.getPw())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "비밀번호 틀렸습니다");
        }
        TokenInfo tokenInfo = JwtTokenUtil.createToken(selectedUser.getUserId(), key, accessTokenExpireTimeMs, refreshTokenExpireTimeMs);
        Map<String, Object> result = new HashMap<>();
        result.put("tokenInfo", tokenInfo);
        result.put("username", selectedUser.getUsername());
        return result;  // 성공시 토큰 발행
    }

    public Map regenerateToken(String refreshToken) {
        // refresh token 정보를 통하여 새로운 토큰을 발급

        // 1. extract userId from refreshToken
        String userId = "";
        try {
            userId = JwtTokenUtil.getUserIdFromToken(refreshToken, key);
        } catch (ExpiredJwtException e) {
            // if token is expired
            throw new AppException(ErrorCode.INVALID_TOKEN, "토큰 만료");
        }
        // 2. find user
        User selectedUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USERID_NOTFOUND, "해당하는 사용자가 없습니다")); //user 없을 때 appexception throw
        // 3. create new token
        TokenInfo tokenInfo = JwtTokenUtil.createToken(selectedUser.getUserId(), key, accessTokenExpireTimeMs, refreshTokenExpireTimeMs);
        Map<String, Object> result = new HashMap<>();
        result.put("tokenInfo", tokenInfo);
        result.put("username", selectedUser.getUsername());
        return result;
    }

    public List<WalletDto> getWalletOverall(User user) {
        return user.getWallets().stream()
                .map(WalletDto::new)
                .collect(Collectors.toList());
    }
    public User resetUser(User user){
        user.getWallets().clear();
        user.getTransactions().clear();
        user.getBanks().clear();
        user.setMoney(10000000);
        user.setDepositCount(0);
        User new_user = userRepository.save(user);
        UserCreatedEvent event = new UserCreatedEvent(new_user);
        eventPublisher.publishEvent(event);
        return new_user;
    }

}
