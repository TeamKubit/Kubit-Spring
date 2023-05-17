package com.konkuk.kubit.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String userId;  // 로그인시 필요한 회원의 id

    @Column(nullable = false, length = 10)
    private String username; // 사용자 이름

    @Column(nullable = false, length = 200)
    private String pw;

    @Column(nullable = false)
    private Date createdAt;

    @Column(nullable = false)
    private int money;
}
