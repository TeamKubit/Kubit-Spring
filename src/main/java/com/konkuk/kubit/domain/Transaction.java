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
    private User user;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="snapshotId")
    private Snapshot snapshotInfo;

    private double quantity;

    private String transactionType;

    private LocalDateTime completeTime;

    private String resultType;

    private double charge;

    @PrePersist
    protected void onCreate() {
        // DB 스키마를 바꿔주는 것은 아니고, spring data jpa를 통해서 create될 때, default 값 생성될 것임
        completeTime = LocalDateTime.now();
    }
}
