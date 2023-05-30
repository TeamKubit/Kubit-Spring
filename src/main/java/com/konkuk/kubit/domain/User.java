package com.konkuk.kubit.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name="uId")
    private Long uId;

    @Column(nullable = false, unique = true, length = 20)
    private String userId;  // 로그인시 필요한 회원의 id

    @Column(nullable = false, length = 10)
    private String username; // 사용자 이름

    @Column(nullable = false, length = 200)
    private String pw;  // 비밀번호

    @Column(nullable = false)
    private double money;   // 잔액

    @Column(nullable = false)
    private int depositCount; // 입금 횟수 <= 3

    @Column(nullable = false, name="created_at")
    private LocalDateTime createdAt;    // 생성 시간

    @OneToMany(mappedBy = "uId", cascade = CascadeType.ALL)
    private List<Wallet> wallets = new ArrayList<>();

    @OneToMany(mappedBy = "uId", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "uId", cascade = CascadeType.ALL)
    private List<Bank> banks = new ArrayList<>();



    @PrePersist
    protected void onCreate() {
        // DB 스키마를 바꿔주는 것은 아니고, spring data jpa를 통해서 create될 때, default 값 생성될 것임
        createdAt = LocalDateTime.now();
    }
}
