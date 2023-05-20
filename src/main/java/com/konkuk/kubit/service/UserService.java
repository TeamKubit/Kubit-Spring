package com.konkuk.kubit.service;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.transaction.Transactional;
import java.util.List;

//@Service
@Transactional
@RequiredArgsConstructor // 어노테이션을 통한 생성자 주입
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public List<User> getUserList() {
        return userRepository.findAll();
    }

    public Long join(String userId, String username, String pw) {
        // id 중복 체크
        userRepository.findByUserId(userId)
                .ifPresent(user -> {
                    throw new RuntimeException(userId + "가 이미 존재합니다.");
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

}
