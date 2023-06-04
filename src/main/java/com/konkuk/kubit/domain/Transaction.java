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
@Table(name="transaction")
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable=false, name="transactionId")
    private Long transactionId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="uId")
    private User uId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="marketCode")
    private Market marketCode;

    private double quantity;

    private String transactionType;

    private LocalDateTime requestTime; // 요청 시간

    private LocalDateTime completeTime; // 체결 시간

    private String resultType;

    private int charge;

    private double requestPrice;

    private double completePrice;

    @PrePersist
    protected void onCreate() {
        // DB 스키마를 바꿔주는 것은 아니고, spring data jpa를 통해서 create될 때, default 값 생성될 것임
        requestTime = LocalDateTime.now();
    }
}
