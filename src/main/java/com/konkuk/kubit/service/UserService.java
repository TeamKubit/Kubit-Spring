package com.konkuk.kubit.service;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.exception.AppException;
import com.konkuk.kubit.exception.ErrorCode;
import com.konkuk.kubit.repository.UserRepository;
import com.konkuk.kubit.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.transaction.Transactional;
import java.util.List;

//@Service
@Transactional
@RequiredArgsConstructor // 어노테이션을 통한 생성자 주입
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    @Value("${jwt.token.secret}") // from application.properties
    private String key;
    private Long tokenExpireTimeMs = 1000 * 60 * 60L; // 1hour

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
                .money(1000000)
                .build();
        userRepository.save(user);
        return user.getId();
    }

    public String login(String userId, String pw) {
        // userId 없음
        User selectedUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USERID_NOTFOUND, userId+"에 해당하는 사용자가 없습니다")); //user 없을 때 appexception throw
        // password 틀림
        if(!encoder.matches(pw, selectedUser.getPw())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "비밀번호 틀렸습니다");
        }
        String token = JwtTokenUtil.createToken(selectedUser.getUserId(),key, tokenExpireTimeMs);
        return token;  // 성공시 토큰 발행
    }

}
