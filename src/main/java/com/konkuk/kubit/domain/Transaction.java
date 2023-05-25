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
    @JoinColumn(name="marketCode")
    private Market marketCode;

    private double quantity;

    private String transactionType;

    private LocalDateTime completeTime;

    private String resultType;

    private double charge;

    private double requestPrice;
}
