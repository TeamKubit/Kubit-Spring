package com.konkuk.kubit.configuration;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.repository.UserRepository;
import com.konkuk.kubit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SpringConfig {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public SpringConfig(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Bean
    public UserService userService() {
        return new UserService(userRepository, encoder);
    }

}
