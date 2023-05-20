package com.konkuk.kubit.service;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

//@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
                .pw(pw)
                .money(1000000)
                .build();
        userRepository.save(user);
        return user.getId();
    }

}
