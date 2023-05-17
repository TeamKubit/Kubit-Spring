package com.konkuk.kubit.repository;

import com.konkuk.kubit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { // <Model, id>
    // spring data jpa 가 interface를 알아서 구현해서 bean에 등록해줌
    Optional<User> findByUserId(String userId);
    Optional<User> findByUsername(String username);
}
