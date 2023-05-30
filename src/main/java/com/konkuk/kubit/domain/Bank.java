package com.konkuk.kubit.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank")
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name="bankId")
    private Long bankId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="uId")
    private User uId;
    private String bankType; // 입금/출금
    private int money; // 입금/출금액
    private LocalDateTime tradeAt;

    @PrePersist
    protected void onCreate() {
        tradeAt = LocalDateTime.now();
    }
}
