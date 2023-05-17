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
    public Long join(User user){
        userRepository.save(user);
        return user.getId();
    }

}
