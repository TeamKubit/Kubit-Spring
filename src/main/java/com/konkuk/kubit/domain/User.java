package com.konkuk.kubit.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String userId;  // 로그인시 필요한 회원의 id

    @Column(nullable = false, length = 10)
    private String username; // 사용자 이름

    @Column(nullable = false, length = 200)
    private String pw;  //비밀번호

    @Column(nullable = false, name="created_at")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int money;


    @PrePersist
    protected void onCreate() {
        // DB 스키마를 바꿔주는 것은 아니고, spring data jpa를 통해서 create될 때, default 값 생성될 것임
        createdAt = LocalDateTime.now();
    }
}
