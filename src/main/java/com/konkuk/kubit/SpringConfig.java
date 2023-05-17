package com.konkuk.kubit;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.repository.UserRepository;
import com.konkuk.kubit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    private final UserRepository userRepository;

    @Autowired
    public SpringConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Bean
    public UserService userService(){
        return new UserService(userRepository);
    }

}
